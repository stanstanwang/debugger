package io.terminus.debugger.client.core;

import io.terminus.debugger.common.msg.TunnelRequest;
import io.terminus.debugger.common.msg.TunnelResponse;
import io.terminus.debugger.common.registry.GetInstanceReq;
import io.terminus.debugger.common.tunnel.RouteConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * debug 客户端请求服务端
 *
 * @author stan
 * @date 2022/3/21
 */
@Component
public class DebugClient {

    private final WebClient webClient;

    public DebugClient(LocalDebugProperties debugProperties, WebClient.Builder builder) {
        String baseUrl = String.format("http://%s:%s", debugProperties.getServerHost(),
                debugProperties.getHttpPort());
        this.webClient = builder.clone()
                .baseUrl(baseUrl).build();
    }


    /**
     * 判断注册中心是否存在
     */
    // TODO test stan 2022/4/22 得判断注册表是否存在
    public boolean instanceExists(GetInstanceReq instanceRequest) {
        return webClient.post()
                .uri(RouteConstants.INSTANCE_EXISTS)
                .bodyValue(instanceRequest)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorReturn(false)
                .blockOptional().orElse(false);
    }

    /**
     * 请求隧道透传数据
     *
     * @param tunnelMessage 隧道消息
     * @return 透传的处理结果
     */
    public <R extends TunnelResponse>
    Mono<R> tunnelRequest(TunnelRequest tunnelMessage, Class<R> resultType) {
        // http 的方式请求隧道服务透传数据
        return webClient.post()
                .uri(RouteConstants.TUNNEL_REQUEST)
                .bodyValue(tunnelMessage)
                .retrieve()
                .bodyToMono(resultType);
    }

}
