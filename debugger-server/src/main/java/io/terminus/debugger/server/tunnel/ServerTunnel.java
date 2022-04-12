package io.terminus.debugger.server.tunnel;

import io.rsocket.RSocket;
import io.terminus.debugger.common.registry.DebuggerConnection;
import io.terminus.debugger.common.registry.DebuggerInstance;
import io.terminus.debugger.common.tunnel.TunnelRoute;
import io.terminus.debugger.server.registry.DebuggerRegistry;
import io.terminus.debugger.common.registry.GetInstanceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import reactor.core.publisher.Mono;

/**
 * 位于服务端的隧道口子
 *
 * @author stan
 * @date 2022/3/21
 */
@Slf4j
public class ServerTunnel {

    private final DebuggerRegistry registry;

    public ServerTunnel(DebuggerRegistry registry) {
        this.registry = registry;
    }

    /**
     * 连接的建立
     */
    @ConnectMapping(TunnelRoute.CONNECT)
    public void connect(RSocketRequester requester, @Payload DebuggerInstance instance) {
        RSocket rSocket = requester.rsocket();
        // 这里onClose语义应该是不太对的，怎么可能是 onClose 的时候去触发注册中心呢
        // 这里效果等同于 Mono.never(), 除非报错，否则不会结束
        rSocket.onClose()
                .doFirst(() -> {
                    log.info("Debugger Client key [{}] connected.", instance.getDebugKey());
                    DebuggerInstance debuggerInstance = new DebuggerInstance(instance.getDebugKey(), instance.getInstanceId(), new DebuggerConnection(requester));
                    registry.register(debuggerInstance);
                })
                .doFinally(signalType -> {
                    log.info("Debugger Client key [{}] disconnected.", instance.getDebugKey());
                    registry.unregister(instance);
                })
                .subscribe();
    }


    /**
     * 将透传请求路由给隧道客户端
     */
    public Mono<Object> send(GetInstanceRequest request,
                             String route,
                             Object tunnelMessage) {
        DebuggerConnection connection = registry
                .get(request)
                .getConnection();
        return connection.send(route, tunnelMessage);
    }

}
