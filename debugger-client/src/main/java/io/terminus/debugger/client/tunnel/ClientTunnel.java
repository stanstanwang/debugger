package io.terminus.debugger.client.tunnel;

import io.terminus.debugger.client.core.DebugClientProperties;
import io.terminus.debugger.client.core.DebugKeyProvider;
import io.terminus.debugger.common.registry.DebuggerInstance;
import io.terminus.debugger.common.tunnel.TunnelRoute;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

/**
 * 位于客户端的隧道口子， 负责与服务端通信
 *
 * @author stan
 * @date 2022/3/21
 */
public class ClientTunnel {

    private DebugClientProperties properties;
    private RSocketRequester.Builder builder;
    private RSocketStrategies strategies;
    private RSocketRequester requester;

    private DebuggerInstance instance;

    public ClientTunnel(RSocketRequester.Builder builder, RSocketStrategies strategies, DebugClientProperties properties, DebugKeyProvider debugKeyProvider) {
        this.properties = properties;
        this.builder = builder;
        this.strategies = strategies;

        if (this.properties.isLocal()) {
            String debugKey = debugKeyProvider.getDebugKey();
            // TODO stan 2022/4/7
            String instanceId = "";
            this.instance = new DebuggerInstance(debugKey, instanceId);
        }
    }

    public void init(List<TunnelHandler> handlers) {
        this.builder = builder.dataMimeType(MimeTypeUtils.APPLICATION_JSON).rsocketConnector(connector -> {
            Object[] handlerArray = handlers.toArray();
            connector.acceptor(RSocketMessageHandler.responder(strategies, handlerArray));
            connector.keepAlive(Duration.ofSeconds(20L), Duration.ofMinutes(1L));
            connector.reconnect(Retry.max(1));
        }).setupRoute(TunnelRoute.CONNECT);
        this.onConnect();
        this.keepAlive();
    }

    private void onConnect() {
        if (this.requester != null) {
            this.requester.rsocketClient().dispose();
        }
        this.requester = this.builder.setupData(instance).tcp(properties.getTunnelHost(), properties.getTunnelPort());
        this.healthCheck().subscribe();
    }

    /**
     * 保活
     */
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


    /**
     * 心跳监测
     *
     * @return 结果
     */
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


}
