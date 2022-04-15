package io.terminus.debugger.common.msg;

import io.terminus.debugger.common.tunnel.RouteConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author stan
 * @date 2022/3/23
 */
@Getter
@Setter
public class HttpTunnelMessage extends TunnelMessage {

    /**
     * 请求的地址，必填
     */
    private String url;

    /**
     * 请求的方式，必填，大写
     */
    private String method;

    /**
     * 对应请求头
     */
    private Map<String, List<String>> headers;

    /**
     * 对应请求 body
     */
    private byte[] body;


    @Override
    public String getRoute() {
        return RouteConstants.HTTP_TUNNEL_HANDLE;
    }
}
