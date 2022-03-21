package io.terminus.debugger.client.http;

import io.terminus.debugger.client.core.DebugClient;
import io.terminus.debugger.client.core.DebugInterceptor;
import io.terminus.debugger.common.msg.TunnelMessage;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * http debug 请求的拦截器
 *
 * @author stan
 * @date 2022/3/21
 */
public class HttpDebugInterceptor extends OncePerRequestFilter implements DebugInterceptor {

    private final DebugClient debugClient;

    public HttpDebugInterceptor(DebugClient debugClient) {
        this.debugClient = debugClient;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 判断是否走debug过滤模式
        // 判断是否携带debug请求头

        // 将http请求转发给debugServer

        TunnelMessage message = new TunnelMessage();

        // 可以是http或者非http
        Object object = debugClient.tunnelRequest(message);

        // 正常就是自己数据，我们这里应该不用额外做处理， write 到 reponse 便可

    }
}
