package io.terminus.debugger.client.tunnel;

import org.springframework.messaging.handler.annotation.MessageMapping;

/**
 * @author stan
 * @date 2022/4/7
 */

public class HttpTunnelHandler implements TunnelHandler{

    // 得在 requester 的地方加上
    @MessageMapping("client-receive")
    public void receive() {

    }

}
