package io.terminus.debugger.common.registry;

import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;

/**
 * 隧道服务端到隧道客户端的连接， 用于从线上给本地的 debugger 发送请求。
 *
 * @author stan
 * @date 2022/4/7
 */
public class DebuggerConnection {

    // TODO stan 2022/4/7 这里定义可能不太好， connection 是 server 才关注的东西
    private final RSocketRequester requester;

    public DebuggerConnection(RSocketRequester requester) {
        this.requester = requester;
    }

    public Mono<Object> send(String route, Object tunnelMessage) {
        // 隧道服务的透传，请求的是哪个地址，由外部决定的
        return requester.route(route)
                .data(tunnelMessage)
                .retrieveMono(Object.class);
    }

    public RSocketRequester getRequester() {
        return requester;
    }

    // TODO stan 2022/4/7 toString 得响应对应到本地的连接
    @Override
    public String toString() {
        return "DebuggerConnection{" +
                "requester=" + requester +
                '}';
    }
}
