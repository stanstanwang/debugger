package io.terminus.debugger.common.msg;

import io.terminus.debugger.common.registry.GetInstanceReq;
import lombok.Data;

/**
 * 隧道透传请求
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
    private Object tunnelMsg;

}
