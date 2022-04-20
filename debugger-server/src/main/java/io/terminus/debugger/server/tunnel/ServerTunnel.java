package io.terminus.debugger.server.tunnel;

import io.rsocket.RSocket;
import io.terminus.debugger.common.registry.*;
import io.terminus.debugger.common.tunnel.RouteConstants;
import io.terminus.debugger.server.registry.DebuggerRegistry;
import io.terminus.debugger.server.registry.ServerDebuggerInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

/**
 * 位于服务端的隧道口子， rsocket 实现
 *
 * @author stan
 * @date 2022/3/21
 */
@Slf4j
@Controller
public class ServerTunnel {

    private final DebuggerRegistry registry;

    public ServerTunnel(DebuggerRegistry registry) {
        this.registry = registry;
    }

    /**
     * 连接的建立
     */
    @ConnectMapping(RouteConstants.CONNECT)
    public void connect(RSocketRequester requester, @Payload DebuggerInstance instance) {
        log.info("client tunnel connected : {}", instance);
        RSocket rSocket = requester.rsocket();
        // 这里onClose语义应该是不太对的，怎么可能是 onClose 的时候去触发注册到注册中心呢
        // 这里效果等同于 Mono.never(), 除非报错，否则不会结束
        // 不过官方用法还真是这样子的， 神奇 https://spring.io/blog/2020/05/12/getting-started-with-rsocket-servers-calling-clients
        rSocket.onClose()
                .doFirst(() -> {
                    log.info("Debugger Client key [{}] connected.", instance.getDebugKey());
                    ServerDebuggerInstance debuggerInstance = new ServerDebuggerInstance(instance.getDebugKey(), instance.getInstanceId(), requester);
                    registry.register(debuggerInstance);
                })
                .doFinally(signalType -> {
                    log.info("Debugger Client key [{}] disconnected.", instance.getDebugKey());
                    registry.unregister(new DelInstanceReq(instance.getDebugKey(), instance.getInstanceId()));
                })
                .subscribe();
    }

    @MessageMapping(RouteConstants.PING)
    public Mono<Integer> ping() {
        return Mono.just(1);
    }


    /**
     * 将透传请求路由给隧道客户端
     *
     * @param getInstanceReq 获取客户端实例
     * @param route          对应要调用的方法
     * @param tunnelMessage  对应的入参
     */
    public Mono<Object> send(GetInstanceReq getInstanceReq,
                             String route,
                             Object tunnelMessage) {
        return registry.get(getInstanceReq).getRequester()
                .route(route)
                .data(tunnelMessage)
                .retrieveMono(Object.class);
    }

}
