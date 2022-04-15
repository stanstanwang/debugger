package io.terminus.debugger.common.msg;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author stan
 * @date 2022/4/12
 */
@Getter
@Setter
public class HttpTunnelResponse extends TunnelResponse {
    /**
     * 状态码
     */
    private int status;

    /**
     * 响应头
     */
    private Map<String, List<String>> headers;

    /**
     * 响应结果
     */
    private byte[] body;
}
