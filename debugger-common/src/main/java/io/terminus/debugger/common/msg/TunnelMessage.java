package io.terminus.debugger.common.msg;

/**
 * 透传的消息
 *
 * @author stan
 * @date 2022/3/21
 */
public abstract class TunnelMessage {

    /**
     * 获取该隧道消息对应需要路由到哪里
     */
    public abstract String getRoute();


}
