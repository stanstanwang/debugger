package io.terminus.debugger.server.rest;

import io.terminus.debugger.common.registry.DebuggerInstance;
import io.terminus.debugger.common.registry.GetInstanceRequest;
import io.terminus.debugger.common.tunnel.RouteConstants;
import io.terminus.debugger.server.registry.DebuggerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collection;

import static io.terminus.debugger.common.tunnel.RouteConstants.TEST_SERVER_CLIENT;

/**
 * @author stan
 * @date 2022/4/15
 */
@Slf4j
@RestController
@RequestMapping("/inner")
// TODO stan 2022/4/15 conditionon
public class InnerController {

    private final DebuggerRegistry registry;

    public InnerController(DebuggerRegistry registry) {
        this.registry = registry;
    }

    /**
     * 查看注册中心列表
     */
    @GetMapping("/registry")
    public Mono<Collection<DebuggerInstance>> registry() {
        return Mono.just(registry.list());
    }

    /**
     * 测试客户端的连接性
     */
    @GetMapping("/server-client/{debugKey}/{instanceId}")
    public Mono<Long> testClient(@PathVariable String debugKey, @PathVariable String instanceId) {
        DebuggerInstance debugInstance = registry.get(new GetInstanceRequest(debugKey, instanceId));
        return debugInstance.getConnection().getRequester()
                .route(TEST_SERVER_CLIENT)
                .data(System.currentTimeMillis())
                .retrieveMono(Long.class);
    }


    @MessageMapping(RouteConstants.TEST_CLIENT_SERVER)
    public Mono<Long> fromClient(Long param) {
        log.info("receive from client {}", param);
        return Mono.just(System.currentTimeMillis());
    }


    @MessageMapping(RouteConstants.PING)
    public Mono<Integer> ping() {
        return Mono.just(1);
    }

    @GetMapping("/dispose")
    public Mono<Long> dispose() {
        registry.list().stream()
                .map(it -> it.getConnection().getRequester())
                .forEach(RSocketRequester::dispose);
        return Mono.empty();
    }


}
