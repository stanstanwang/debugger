package io.terminus.debugger.common.tunnel;

/**
 * 路由的常量
 *
 * @author stan
 * @date 2022/4/7
 */
public interface RouteConstants {

    /**
     * 请求隧道透传服务
     */
    String INSTANCE_EXISTS = "/instanceExists";
    String TUNNEL_REQUEST = "/tunnelRequest";

    /**
     * 服务端处理隧道的连接
     */
    // RSocket 连接的建立
    String CONNECT = "tunnel-connect";
    // 心跳使用
    String PING = "ping";

    /**
     * 客户端处理回调
     */
    String HTTP_TUNNEL_HANDLE = "http-tunnel-handle";
    String INVOKE_TUNNEL_HANDLE = "invoke-tunnel-handle";

    /**
     * 测试使用
     */
    String TEST_SERVER_CLIENT = "test-server-client";
    String TEST_CLIENT_SERVER = "test-client-server";

}
