package io.terminus.debugger.client.core;

import io.terminus.debugger.common.msg.TunnelMessage;
import io.terminus.debugger.common.msg.TunnelRequest;
import io.terminus.debugger.common.msg.TunnelResponse;
import io.terminus.debugger.common.registry.GetInstanceRequest;
import io.terminus.debugger.common.tunnel.RouteConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class DebugClient implements ApplicationContextAware {

    private final WebClient webClient;
    private ApplicationContext ac;

    // TODO stan 2022/4/13 使用 webClientBuilder?
    public DebugClient(DebugClientProperties clientProperties, WebClient.Builder builder) {
        String baseUrl = String.format("http://%s:%s", clientProperties.getServerHost(),
                clientProperties.getHttpPort());
        this.webClient = builder.baseUrl(baseUrl).build();
    }


    /**
     * 请求隧道透传数据
     *
     * @param tunnelMessage 隧道消息
     * @return 透传的处理结果
     */
    public <T extends TunnelMessage, R extends TunnelResponse>
    Mono<R> tunnelRequest(T tunnelMessage, Class<R> resultType) {
        // 准备请求
        TunnelRequest tunnelRequest = new TunnelRequest();
        tunnelRequest.setInstanceRequest(
                new GetInstanceRequest(DebugKeyContext.get(), ac.getId())
        );
        tunnelRequest.setRoute(tunnelMessage.getRoute());
        tunnelRequest.setTunnelMsg(tunnelMessage);

        // http 的方式请求隧道服务透传数据
        // TODO stan 2022/4/12 会判断注册表看是否执行成功
        return webClient.post()
                .uri(RouteConstants.TUNNEL_REQUEST)
                .bodyValue(tunnelRequest)
                .retrieve()
                .bodyToMono(resultType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
