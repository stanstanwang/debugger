package io.terminus.debugger.client.tunnel;

import io.terminus.debugger.common.msg.HttpTunnelMessage;
import io.terminus.debugger.common.msg.HttpTunnelResponse;
import io.terminus.debugger.common.tunnel.RouteConstants;
import org.springframework.messaging.handler.annotation.MessageMapping;
import reactor.core.publisher.Mono;

/**
 * @author stan
 * @date 2022/4/7
 */
public class InvokeTunnelHandler implements TunnelHandler {

    @MessageMapping(RouteConstants.INVOKE_TUNNEL_HANDLE)
    public Mono<HttpTunnelResponse> receive(HttpTunnelMessage tunnelMessage) {
        // not yet implement
        return null;
    }

}
