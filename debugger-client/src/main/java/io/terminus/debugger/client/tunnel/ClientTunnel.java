package io.terminus.debugger.client.tunnel;

import io.terminus.debugger.client.core.DebugClientProperties;
import io.terminus.debugger.client.core.DebugKeyProvider;
import io.terminus.debugger.common.registry.DebuggerInstance;
import io.terminus.debugger.common.tunnel.RouteConstants;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

/**
 * 位于客户端的隧道口子， 负责与服务端通信。
 *
 * @author stan
 * @date 2022/3/21
 */
@Component
public class ClientTunnel {

    private final DebugKeyProvider debugKeyProvider;
    private final DebugClientProperties properties;
    private RSocketRequester.Builder builder;
    private RSocketRequester requester;

    // TODO stan 2022/4/15
    // @Value("${spring.application.name}")
    private String applicationId = "helloApplicationId";


    private DebuggerInstance instance;

    public ClientTunnel(RSocketRequester.Builder builder,
                        RSocketStrategies strategies,
                        DebugClientProperties properties, DebugKeyProvider debugKeyProvider,
                        List<TunnelHandler> handlers
    ) {
        this.properties = properties;
        this.debugKeyProvider = debugKeyProvider;
        this.builder = builder;
        initBuilder(strategies, handlers);
        this.requester = connect();
    }

    private RSocketRequester connect() {
        return this.builder.setupData(getDebugInstance())
                .tcp(properties.getServerHost(), properties.getTunnelPort());
    }


    /**
     * 获取当前服务实例
     */
    private DebuggerInstance getDebugInstance() {
        if (this.instance == null) {
            String debugKey = this.debugKeyProvider.getDebugKey();
            String instanceId = applicationId;
            this.instance = new DebuggerInstance(debugKey, instanceId);
        }
        return instance;
    }

    public void initBuilder(RSocketStrategies strategies, List<TunnelHandler> handlers) {
        this.builder = builder.dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .rsocketConnector(connector -> {
                    Object[] handlerArray = handlers.toArray();
                    connector.acceptor(RSocketMessageHandler.responder(strategies, handlerArray));
                    connector.keepAlive(Duration.ofSeconds(20L), Duration.ofMinutes(1L));
                    connector.reconnect(Retry.max(1));
                }).setupRoute(RouteConstants.CONNECT)
        ;
    }


    /*
    public void init() {
        this.onConnect();
        this.keepAlive();
    }*/


/**
 * 初始化客户端
 *//*

    private void initClient() {
        this.requester.rsocketClient().source()
                .doOnSuccess(socket -> {
                    this.active = true;
                    log.info("Debug client key [{}] connection success.", namespace.getKey());
                    socket.onClose().doFinally(signalType -> {
                        log.info("Debug client key [{}] connection disconnected.", namespace.getKey());
                        onClosed(socket);
                    }).subscribe();
                })
                .doOnError(e -> {
                    log.warn("Debug client key[{}] connection fail.", namespace.getKey(), e);
                    this.active = false;
                    this.tryToReconnect();
                }).subscribe();
    }

    */
/**
 * 心跳监测
 *
 * @return 结果
 *//*

    private Mono<Boolean> healthCheck() {
        if (this.stop) {
            return Mono.just(false);
        }
        return this.requester.route(CLIENT_CALL_SERVER_PONG).data("").retrieveMono(Integer.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorReturn(0)
                .handle((payload, sink) -> {
                    if (payload == 1 && !this.active) {
                        this.initClient();
                    }
                    sink.next(payload == 1);
                });
    }

    */
/**
 * 保活
 *//*

    private void keepAlive() {
        Flux.interval(Duration.ofSeconds(15))
                .filter(sequence -> !active)
                .flatMap(s -> healthCheck())
                .subscribe(check -> {
                    if (!check && !stop) {
                        onConnect();
                    }
                });
    }

    */
/**
 * 尝试重联
 *//*

    public void tryToReconnect() {
        if (retry.get()) {
            log.info("Debug client tryToReconnect...");
            return;
        }
        retry.set(true);
        Flux.range(1, MAX_TRY_COUNT)
                .delayElements(Duration.ofSeconds(5))
                .filter(id -> !active)
                .flatMap(id -> healthCheck())
                .filter(check -> {
                    if (check) {
                        active = true;
                    }
                    return !check;
                })
                .doFinally(s -> {
                    log.info("Debug client status: {}:{}", active, s);
                    retry.set(false);
                })
                .subscribe(check -> {
                    if (!active && !stop) {
                        log.info("Debug client reconnect...");
                        onConnect();
                    }
                });
    }

    */

    /**
     * 关闭链接，后面会重试链接
     *
     * @param rsocket 链接
     *//*

    private void onClosed(RSocket rsocket) {
        if (!rsocket.isDisposed()) {
            try {
                rsocket.dispose();
            } catch (Exception ignore) {

            }
        }
        this.active = false;

        tryToReconnect();
    }
*/
    public RSocketRequester getRequester() {
        return requester;
    }
}
