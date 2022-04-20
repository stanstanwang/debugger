package io.terminus.debugger.client.tunnel;

import io.terminus.debugger.client.http.HttpDebugRepeater;
import io.terminus.debugger.common.msg.HttpTunnelMessage;
import io.terminus.debugger.common.msg.HttpTunnelResponse;
import io.terminus.debugger.common.tunnel.RouteConstants;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author stan
 * @date 2022/4/7
 */
@Component
public class HttpTunnelHandler implements TunnelHandler {

    private final HttpDebugRepeater httpDebugRepeater;

    public HttpTunnelHandler(HttpDebugRepeater httpDebugRepeater) {
        this.httpDebugRepeater = httpDebugRepeater;
    }

    @MessageMapping(RouteConstants.HTTP_TUNNEL_HANDLE)
    public Mono<HttpTunnelResponse> receive(HttpTunnelMessage tunnelMessage) {
        return httpDebugRepeater.repeat(tunnelMessage);
    }

}
