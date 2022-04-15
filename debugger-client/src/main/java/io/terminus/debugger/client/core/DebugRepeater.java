package io.terminus.debugger.client.core;

import reactor.core.publisher.Mono;

/**
 * debug 的请求回放器。
 * 将请求转给本地之后，适配不同协议(http/dubbo/mq)的回放器。
 *
 * @author stan
 * @date 2022/3/21
 */
public interface DebugRepeater<T, R> {

    /**
     * 回放请求
     *
     * @return 因为是采用 nio 的方式回放请求，期望回放的处理方式也是nio模式， 所以这里使用 Mono 作为响应结果
     */
    Mono<R> repeat(T message);

}
