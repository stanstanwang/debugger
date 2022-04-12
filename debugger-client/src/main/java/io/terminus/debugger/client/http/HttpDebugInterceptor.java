package io.terminus.debugger.client.http;

import io.terminus.debugger.client.core.DebugClient;
import io.terminus.debugger.client.core.DebugClientProperties;
import io.terminus.debugger.client.core.DebugInterceptor;
import io.terminus.debugger.client.core.DebugKeyContext;
import io.terminus.debugger.common.msg.TunnelMessage;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static io.terminus.debugger.client.core.DebugKeyContext.DEBUG_KEY;

/**
 * http debug 请求的拦截器
 *
 * @author stan
 * @date 2022/3/21
 */
public class HttpDebugInterceptor extends OncePerRequestFilter implements DebugInterceptor {

    private final DebugClient debugClient;
    private final DebugClientProperties clientProperties;

    public HttpDebugInterceptor(DebugClient debugClient, DebugClientProperties clientProperties) {
        this.debugClient = debugClient;
        this.clientProperties = clientProperties;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        try {
            if (debugEnable(request)) {
                localDebug(request, response);
            } else {
                filterChain.doFilter(request, response);
            }
        } finally {
            // TODO stan 2022/4/11 代码格式没太好，有看看具体怎么改
            DebugKeyContext.remove();
        }
    }


    /**
     * 判断是否执行 localdebug 的逻辑
     */
    private boolean debugEnable(HttpServletRequest request) {
        // 判断是否走debug模式
        if (!clientProperties.isEnable()) {
            return false;
        }

        // 本身是 local 的情况下
        if (clientProperties.isLocal()) {
            return false;
        }

        // 判断是否携带debug请求头
        String debugKey = request.getHeader(DEBUG_KEY);
        if (StringUtils.hasLength(debugKey)) {
            DebugKeyContext.set(debugKey);
            return true;
        }
        return false;
    }


    private void localDebug(HttpServletRequest request, HttpServletResponse response) {
        // 将http请求转发给debugServer
        TunnelMessage message = new TunnelMessage();

        // 可以是http或者非http
        Object object = debugClient.tunnelRequest(message);

        // 正常应该就是 proxy 的模式， 响应的应该就是 response
        // response.w
    }


}
