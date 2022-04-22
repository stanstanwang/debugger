package io.terminus.debugger.server.core;

import io.terminus.debugger.common.msg.TunnelRequest;
import io.terminus.debugger.common.registry.GetInstanceReq;
import io.terminus.debugger.common.tunnel.RouteConstants;
import io.terminus.debugger.server.registry.DebuggerRegistry;
import io.terminus.debugger.server.tunnel.ServerTunnel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * debug 服务端响应 debug 客户端的请求
 *
 * @author stan
 * @date 2022/3/21
 */
@RestController
@RequiredArgsConstructor
public class DebugServer {

    private final DebuggerRegistry debuggerRegistry;

    private final ServerTunnel serverTunnel;

    /**
     * 判断对应实例是否已经注册到注册中心
     */
    @PostMapping(RouteConstants.INSTANCE_EXISTS)
    public Mono<Boolean> instanceExists(@RequestBody GetInstanceReq instanceReq) {
        return Mono.just(debuggerRegistry.get(instanceReq) != null);
    }


    /**
     * 处理透传请求， 透传服务端将数据透传给对应本地
     */
    @PostMapping(RouteConstants.TUNNEL_REQUEST)
    public Mono<Object> tunnelRequest(@RequestBody TunnelRequest tunnelRequest) {
        return serverTunnel.send(tunnelRequest.getInstanceRequest(),
                tunnelRequest.getRoute(),
                tunnelRequest.getTunnelMsg());
    }


}
