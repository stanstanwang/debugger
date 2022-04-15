package io.terminus.debugger.client.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author stan
 * @date 2022/4/7
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "terminus.localdebug")
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
    private String serverHost = "127.0.0.1";

    /**
     * http服务的端口
     */
    private int httpPort = 8080;

    /**
     * 隧道服务的端口，目前是 roscket 的实现
     */
    private int tunnelPort = 7000;
}
