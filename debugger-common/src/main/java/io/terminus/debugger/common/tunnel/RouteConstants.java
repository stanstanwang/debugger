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
    String TUNNEL_REQUEST = "/tunnelRequest";

    /**
     * 隧道的连接
     */
    String CONNECT = "tunnel-connect";

    /**
     * 处理回调
     */
    String HTTP_TUNNEL_HANDLE = "http-tunnel-handle";
    String INVOKE_TUNNEL_HANDLE = "invoke-tunnel-handle";

    /**
     * 测试使用
     */
    String TEST_SERVER_CLIENT = "test-server-client";
    String TEST_CLIENT_SERVER = "test-client-server";

}
