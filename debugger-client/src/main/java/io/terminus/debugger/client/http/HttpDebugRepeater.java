package io.terminus.debugger.client.http;

import io.terminus.debugger.client.core.DebugRepeater;
import io.terminus.debugger.common.msg.HttpTunnelMessage;
import io.terminus.debugger.common.msg.HttpTunnelResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * @author stan
 * @date 2022/3/21
 */
@Component
// TODO stan 2022/4/19 是否客户端才必须有
public class HttpDebugRepeater implements DebugRepeater<HttpTunnelMessage, HttpTunnelResponse> {


    private final WebClient webClient;


    public HttpDebugRepeater(@Value("${server.port}") Integer port) {
        // 客户端会内嵌到用户的jvm里边的，所以这个可以取 web 容器里边配置的 http 端口
        this.webClient = WebClient.builder()
                .baseUrl(String.format("http://127.0.0.1:%s", port))
                .build();
    }

    @Override
    public Mono<HttpTunnelResponse> repeat(HttpTunnelMessage message) {
        return webClient.method(HttpMethod.valueOf(message.getMethod()))
                .uri(message.getUrl())
                .headers(h -> message.getHeaders().forEach(h::addAll))
                .bodyValue(message.getBody())
                .exchangeToMono(this::toTunnelResponse);
    }


    // 参考 org.springframework.web.reactive.function.client.WebClientUtils.mapToEntity 的实现
    // 透传的话完整响应本地处理结果便可，不管是否正确
    private Mono<HttpTunnelResponse> toTunnelResponse(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(byte[].class)
                .defaultIfEmpty(EMPTY)
                .map(b -> {
                    HttpTunnelResponse response = new HttpTunnelResponse();
                    response.setStatus(clientResponse.statusCode().value());
                    response.setHeaders(new HashMap<>(clientResponse.headers().asHttpHeaders()));
                    response.setBody(b != EMPTY ? b : null);
                    return response;
                });
    }


    private static final byte[] EMPTY = new byte[0];

}
