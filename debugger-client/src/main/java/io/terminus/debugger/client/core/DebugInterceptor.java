package io.terminus.debugger.client.core;

/**
 * debug 的请求拦截器。
 * 在跑真正业务逻辑的时候做拦截，判断是否需要将请求转给本地处理。
 *
 * @author stan
 * @date 2022/3/21
 */
public interface DebugInterceptor {
}
