package io.terminus.debugger.client.rest;

import io.terminus.debugger.client.core.DebuggerInstanceProvider;
import io.terminus.debugger.client.tunnel.ClientTunnel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static io.terminus.debugger.common.tunnel.RouteConstants.TEST_CLIENT_SERVER;

/**
 * @author stan
 * @date 2022/4/15
 */
@RestController
@RequestMapping("/inner")
public class InnerController {


    private final ClientTunnel clientTunnel;

    private final DebuggerInstanceProvider debuggerInstanceProvider;


    public InnerController(Optional<ClientTunnel> clientTunnel, DebuggerInstanceProvider debuggerInstanceProvider) {
        this.clientTunnel = clientTunnel.orElse(null);
        this.debuggerInstanceProvider = debuggerInstanceProvider;
    }

    /**
     * 获取当前的debugKey
     */
    @GetMapping("/debugKey")
    public Mono<String> debugKey() {
        return Mono.just(debuggerInstanceProvider.getDebugKey());
    }

    /**
     * 测试客户端的连接性
     */
    @GetMapping("/client-server")
    public Mono<Long> testServer() {
        if (clientTunnel == null) {
            return Mono.empty();
        }
        return clientTunnel.getRequester()
                .route(TEST_CLIENT_SERVER)
                .data(System.currentTimeMillis())
                .retrieveMono(Long.class);
    }


    @GetMapping("/dispose")
    public Mono<Long> dispose() {
        if (clientTunnel == null) {
            return Mono.empty();
        }
        clientTunnel.getRequester().dispose();
        return Mono.empty();
    }

}
