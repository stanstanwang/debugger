package io.terminus.debugger.client.core;

import org.springframework.stereotype.Component;

/**
 * @author stan
 * @date 2022/4/7
 */
@Component
public class DebugKeyProvider {


    /**
     * 可以通过 mac 地址获取 debugKey， 也可以通过配置文件获取 debugKey
     */
    public String getDebugKey() {
        // TODO stan 2022/4/15 通过环境变量取， 没有环境变量则取 mac 地址
        return "stan_123";
    }
}
