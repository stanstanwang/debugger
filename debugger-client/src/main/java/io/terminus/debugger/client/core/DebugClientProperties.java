package io.terminus.debugger.client.core;

/**
 * @author stan
 * @date 2022/4/7
 */
public class DebugClientProperties {

    /**
     * 当前debug功能是否开启
     */
    private boolean enable = false;

    /**
     * 当前 debug 客户端是不是本地
     */
    private boolean local = false;

    // TODO stan 2022/4/11 tunnel 还是 debugger， 要明确下
    // debug server 是线上到线上的
    // 隧道是线上到本地的
    /**
     * 隧道服务
     */
    private String tunnelHost;

    /**
     * 隧道服务对应的端口
     */
    private int tunnelPort;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getTunnelHost() {
        return tunnelHost;
    }

    public void setTunnelHost(String tunnelHost) {
        this.tunnelHost = tunnelHost;
    }

    public int getTunnelPort() {
        return tunnelPort;
    }

    public void setTunnelPort(int tunnelPort) {
        this.tunnelPort = tunnelPort;
    }
}
