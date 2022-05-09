package io.terminus.debugger.client.core;

/**
 * debug 的请求拦截器， 在跑真正业务逻辑的时候做拦截。
 * <p>
 * 如判断远程的调用是否要拦截回放到本地，本地的调用是否要做拦截转发给远程帮忙调用。
 *
 * @author stan
 * @date 2022/3/21
 */
public interface DebugInterceptor {

}
