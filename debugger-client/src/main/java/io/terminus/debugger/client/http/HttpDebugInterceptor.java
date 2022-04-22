package io.terminus.debugger.client.http;

import io.terminus.debugger.client.core.*;
import io.terminus.debugger.common.msg.HttpTunnelMessage;
import io.terminus.debugger.common.msg.HttpTunnelResponse;
import io.terminus.debugger.common.msg.TunnelRequest;
import io.terminus.debugger.common.registry.GetInstanceReq;
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
    private final LocalDebugProperties debugProperties;
    private final DebuggerInstanceProvider instanceProvider;

    public HttpDebugInterceptor(DebugClient debugClient,
                                LocalDebugProperties debugProperties, DebuggerInstanceProvider instanceProvider) {
        this.debugClient = debugClient;
        this.debugProperties = debugProperties;
        this.instanceProvider = instanceProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 可以确保所有http请求都经过该拦截器， DebugKeyContext 都有值
            initDebugContext(request);
            if (canLocalDebug()) {
                // 因为里边用到了nio去转发， 所以这里也得转异步
                AsyncContext asyncContext = request.startAsync();
                localDebug(asyncContext, (HttpServletRequest) asyncContext.getRequest(),
                        (HttpServletResponse) asyncContext.getResponse());
            } else {
                filterChain.doFilter(request, response);
            }
        } finally {
            DebugKeyContext.remove();
        }
    }

    private boolean canLocalDebug() {
        if (!debugProperties.isLocalDebugEnable()) {
            return false;
        }
        return debugClient.instanceExists(getInstanceReq());
    }


    private void initDebugContext(HttpServletRequest request) {
        String debugKey = request.getHeader(DEBUG_KEY);
        if (StringUtils.hasLength(debugKey)) {
            DebugKeyContext.set(debugKey);
        }
    }


    private void localDebug(AsyncContext asyncContext, HttpServletRequest request, HttpServletResponse response) {
        // 1. 将 http request 透传给隧道服务
        HttpTunnelMessage tunnelMessage = convertRequest(request);
        TunnelRequest tunnelRequest = TunnelRequest.wrapMessage(getInstanceReq(), tunnelMessage);

        // 2. 将 http response 响应给正常请求
        // 这里得异步处理，避免阻塞http线程， 本身底层webClient的实现就是非阻塞的，应该还好
        // 执行失败不用处理， 因为转发的是整个 http 请求
        // TODO test stan 2022/4/13 超时异常的处理
        debugClient.tunnelRequest(tunnelRequest, HttpTunnelResponse.class)
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


    private GetInstanceReq getInstanceReq() {
        return new GetInstanceReq(DebugKeyContext.get(), instanceProvider.getInstanceId());
    }

}
