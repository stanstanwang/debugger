package io.terminus.debugger.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 当前应用为localdebug的local模式时，禁用像注册中心注册的功能，这样不会污染线上的环境
 *
 * @author stan
 * @date 2022/5/9
 */
public class DisableRegisterEnvProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Boolean local = environment.getProperty("terminus.localdebug.local", Boolean.class);
        if (local == null || !local) {
            return;
        }


        Map<String, Object> map = new HashMap<>();
        // 参考 com.alibaba.cloud.nacos.NacosDiscoveryProperties#registerEnabled
        map.put("spring.cloud.nacos.discovery.register-enabled", false);
        // 参考 org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties#register
        map.put("spring.cloud.consul.discovery.register", false);
        // 参考 org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties#register
        map.put("spring.cloud.zookeeper.discovery.register", false);

        environment.getPropertySources()
                .addLast(new MapPropertySource("disable register with discover server", map));
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
