package io.terminus.debugger.client.tunnel;

import io.terminus.debugger.common.tunnel.RouteConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 这里存放服务端到客户端的通用处理器
 *
 * @author stan
 * @date 2022/4/15
 */
@Component
@Slf4j
public class ServerClientHandler implements TunnelHandler {

    @MessageMapping(RouteConstants.TEST_SERVER_CLIENT)
    public Mono<Long> fromServer(Long param) {
        log.info("receive from server {}", param);
        return Mono.just(System.currentTimeMillis());
    }
}
