package io.terminus.debugger.common.msg;

import io.terminus.debugger.common.registry.GetInstanceReq;
import lombok.Data;

/**
 * 透传的请求， 需要告知透传给谁，哪个地址
 * 透传的请求会包含 {@link TunnelMessage}
 *
 * @author stan
 * @date 2022/4/7
 */
@Data
public class TunnelRequest {

    /**
     * 透传给对应的实例
     */
    private GetInstanceReq instanceRequest;

    /**
     * 透传给对应的地址
     */
    private String route;

    /**
     * 对应透传的消息
     */
    private TunnelMessage tunnelMsg;


    public static TunnelRequest wrapMessage(GetInstanceReq getInstanceReq, TunnelMessage tunnelMessage) {
        // 准备请求
        TunnelRequest tunnelRequest = new TunnelRequest();
        tunnelRequest.setInstanceRequest(getInstanceReq);
        tunnelRequest.setRoute(tunnelMessage.getRoute());
        tunnelRequest.setTunnelMsg(tunnelMessage);
        return tunnelRequest;
    }

}
