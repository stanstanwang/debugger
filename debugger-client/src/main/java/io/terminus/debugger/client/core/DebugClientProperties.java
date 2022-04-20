package io.terminus.debugger.client.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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


    /**
     * 判断是否执行 localDebug 的逻辑
     */
    public boolean isLocalDebugEnable() {
        // 判断是否走debug模式
        if (!this.isEnable()) {
            return false;
        }

        // 本身是 local 的情况下
        if (this.isLocal()) {
            return false;
        }

        // 判断是否携带debug请求头
        return StringUtils.hasLength(DebugKeyContext.get());
    }

}
