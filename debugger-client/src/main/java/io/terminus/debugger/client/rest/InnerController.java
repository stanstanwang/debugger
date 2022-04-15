package io.terminus.debugger.client.rest;

import io.terminus.debugger.client.tunnel.ClientTunnel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static io.terminus.debugger.common.tunnel.RouteConstants.TEST_CLIENT_SERVER;

/**
 * @author stan
 * @date 2022/4/15
 */
@RestController
@RequestMapping("/inner")
// TODO stan 2022/4/15 conditionon
public class InnerController {


    private final ClientTunnel clientTunnel;

    public InnerController(ClientTunnel clientTunnel) {
        this.clientTunnel = clientTunnel;
    }

    /**
     * 测试客户端的连接性
     */
    @GetMapping("/client-server")
    public Mono<Long> testServer() {
        return clientTunnel.getRequester()
                .route(TEST_CLIENT_SERVER)
                .data(System.currentTimeMillis())
                .retrieveMono(Long.class);
    }

}
