package io.terminus.debugger.client.http;

import io.terminus.debugger.client.core.DebugRepeater;
import io.terminus.debugger.common.msg.TunnelMessage;

/**
 * @author stan
 * @date 2022/3/21
 */
public class HttpRepeater implements DebugRepeater {


    // 接口会是这种模式么？
    public Object handle(TunnelMessage tunnelMessage){

        // 重新发起http请求

        // 参考 org.springframework.cloud.gateway.filter.WebClientHttpRoutingFilter#filter

        return null;
    }
}
