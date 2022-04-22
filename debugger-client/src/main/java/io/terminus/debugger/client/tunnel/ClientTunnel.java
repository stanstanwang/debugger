package io.terminus.debugger.client.tunnel;

import io.terminus.debugger.client.core.DebuggerInstanceProvider;
import io.terminus.debugger.client.core.LocalDebugProperties;
import io.terminus.debugger.common.tunnel.RouteConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;

/**
 * 位于客户端的隧道口子， 负责与服务端通信。
 *
 * @author stan
 * @date 2022/3/21
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "terminus.localdebug", value = "local", havingValue = "true")
public class ClientTunnel {

    private final DebuggerInstanceProvider instanceProvider;
    private final LocalDebugProperties debugProperties;
    private final RSocketRequester requester;

    public ClientTunnel(RSocketRequester.Builder builder,
                        RSocketStrategies strategies,
                        List<TunnelHandler> handlers,
                        DebuggerInstanceProvider instanceProvider, LocalDebugProperties debugProperties
    ) {
        this.debugProperties = debugProperties;
        this.instanceProvider = instanceProvider;
        this.requester = connect(builder, strategies, handlers);
        triggerReconnect();
    }

    /**
     * 建立 rsocket 连接
     *
     * @param builder    用于构建 rsocket 的请求
     * @param strategies 用于做配置， 比如序列化之类的
     * @param handlers   默认处理器
     */
    private RSocketRequester connect(RSocketRequester.Builder builder, RSocketStrategies strategies, List<TunnelHandler> handlers) {
        return builder.dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .rsocketConnector(connector -> {
                    Object[] handlerArray = handlers.toArray();
                    connector.acceptor(RSocketMessageHandler.responder(strategies, handlerArray));
                    // 默认服务端和客户端 20秒发送一次心跳， 90秒内没接受到心跳则会 close
                    // connector.keepAlive(Duration.ofSeconds(5L), Duration.ofSeconds(10L));
                    // 配置无限重试
                    connector.reconnect(Retry.indefinitely());
                })
                .setupRoute(RouteConstants.CONNECT)
                .setupData(instanceProvider.get())
                // 内部还是 Mono， subscribe前并不会真正触发操作
                .tcp(debugProperties.getServerHost(), debugProperties.getTunnelPort());
    }


    /**
     * 触发重连操作， rsocket 是需要发送请求才能触发 setUp 包的，这里通过定时器来不断发送请求
     * <p>
     * 这样可以保证以下情况下的连接稳定：
     * - 服务端重启， 客户端会重新发送 setup 请求
     * - 服务端或者客户端因心跳超时关闭， 客户端也会重新发送 setup 请求
     */
    private void triggerReconnect() {
        Flux.interval(Duration.ofSeconds(5), Schedulers.newParallel("debugger-reconnect", 1))
                // 这样发生错误之后， interval 还能继续
                .flatMap(this::pingWithCatch)
                .subscribe();
    }

    // 响应 0 表示连接不成功
    private Mono<Integer> pingWithCatch(long v) {
        if (requester.isDisposed()) {
            return Mono.just(0);
        }
        return this.requester.route(RouteConstants.PING)
                .retrieveMono(Integer.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> {
                    log.error("triggerReconnect error", e);
                    return Mono.just(0);
                });
    }

    public RSocketRequester getRequester() {
        return requester;
    }


    // 貌似不用加这个服务端也能感知到 close, 神奇了
    @PreDestroy
    public void destroy() {
        log.info("dispose {}", requester.isDisposed());
        if (!requester.isDisposed()) {
            requester.dispose();
        }
    }

}
