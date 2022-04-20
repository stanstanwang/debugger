package io.terminus.debugger.client.core;

import io.terminus.debugger.common.msg.TunnelRequest;
import io.terminus.debugger.common.msg.TunnelResponse;
import io.terminus.debugger.common.tunnel.RouteConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * debug 客户端请求服务端
 *
 * @author stan
 * @date 2022/3/21
 */
@Component
public class DebugClient {

    private final WebClient webClient;

    public DebugClient(DebugClientProperties clientProperties, WebClient.Builder builder) {
        String baseUrl = String.format("http://%s:%s", clientProperties.getServerHost(),
                clientProperties.getHttpPort());
        this.webClient = builder.clone()
                .baseUrl(baseUrl).build();
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
        // TODO stan 2022/4/12 会判断注册表看是否执行成功
        return webClient.post()
                .uri(RouteConstants.TUNNEL_REQUEST)
                .bodyValue(tunnelMessage)
                .retrieve()
                .bodyToMono(resultType);
    }

}
