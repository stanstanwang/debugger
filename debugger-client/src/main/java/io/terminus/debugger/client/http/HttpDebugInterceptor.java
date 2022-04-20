package io.terminus.debugger.client.http;

import io.terminus.debugger.client.core.DebugClient;
import io.terminus.debugger.client.core.DebugClientProperties;
import io.terminus.debugger.client.core.DebugInterceptor;
import io.terminus.debugger.client.core.DebugKeyContext;
import io.terminus.debugger.common.msg.HttpTunnelMessage;
import io.terminus.debugger.common.msg.HttpTunnelResponse;
import lombok.SneakyThrows;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.AsyncContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

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
                AsyncContext asyncContext = request.startAsync();
                // 因为里边用到了nio去转发， 所以这里也得转异步
                localDebug(asyncContext, (HttpServletRequest) asyncContext.getRequest(),
                        (HttpServletResponse) asyncContext.getResponse());
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


    private void localDebug(AsyncContext asyncContext, HttpServletRequest request, HttpServletResponse response) {
        // 1. 将 http request 透传给隧道服务
        HttpTunnelMessage tunnelMessage = convertRequest(request);

        // TODO stan 2022/4/12 这里得异步处理，避免阻塞http线程或者超时， 本身底层webClient的实现就是非阻塞的，应该还好
        // TODO stan 2022/4/13 超时异常的处理
        // TODO stan 2022/4/13 执行失败的判断

        // 2. 将 http response 响应给正常请求
        debugClient.tunnelRequest(tunnelMessage, HttpTunnelResponse.class)
                .timeout(Duration.ofSeconds(20))
                .doFinally(s -> asyncContext.complete())
                .subscribe(r -> convertResponse(r, response))
        ;
    }


    /**
     * 将 http 请求转化为隧道透传的请求
     *
     * @param request servlet 的 http 请求
     */
    @SneakyThrows
    private HttpTunnelMessage convertRequest(HttpServletRequest request) {
        HttpTunnelMessage tunnelMessage = new HttpTunnelMessage();
        // TODO test stan 2022/4/12 getString() 的 验证， 可能会少了 params
        tunnelMessage.setUrl(getUrl(request));
        tunnelMessage.setMethod(request.getMethod());
        tunnelMessage.setHeaders(toHeaders(request));

        // TODO test stan 2022/4/13 是否有可重复的需求
        int contentLength = request.getContentLength();
        ByteArrayOutputStream boas = new ByteArrayOutputStream(contentLength > 0 ?
                contentLength : StreamUtils.BUFFER_SIZE);
        StreamUtils.copy(request.getInputStream(), boas);
        tunnelMessage.setBody(boas.toByteArray());
        return tunnelMessage;
    }


    /**
     * 获取 url， 得将 queryString 这部分拼接回来
     */
    private String getUrl(HttpServletRequest request) {
        String url;
        if (StringUtils.hasLength(request.getQueryString())) {
            url = request.getRequestURI() + "?" + request.getQueryString();
        } else {
            url = request.getRequestURI();
        }
        return url;
    }


    /**
     * 将隧道响应的结果， 转换为 http 响应
     *
     * @param tunnelResponse 隧道响应的结果
     * @param response       servlet 的 http 响应
     */
    @SneakyThrows
    private void convertResponse(HttpTunnelResponse tunnelResponse, HttpServletResponse response) {
        response.setStatus(tunnelResponse.getStatus());
        tunnelResponse.getHeaders().forEach((k, vs) -> {
            vs.forEach(v -> response.addHeader(k, v));
        });
        StreamUtils.copy(tunnelResponse.getBody(), response.getOutputStream());
    }


    private Map<String, List<String>> toHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            List<String> values = toCollection(request.getHeaders(key));
            headers.put(key, values);
        }
        return headers;
    }

    private List<String> toCollection(Enumeration<String> values) {
        List<String> list = new ArrayList<>(5);
        while (values.hasMoreElements()) {
            list.add(values.nextElement());
        }
        return list;
    }


}
