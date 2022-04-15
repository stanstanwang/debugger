package io.terminus.debugger.client;

import io.terminus.debugger.client.core.DebugClient;
import io.terminus.debugger.client.core.DebugClientProperties;
import io.terminus.debugger.client.http.HttpDebugInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author stan
 * @date 2022/4/15
 */
@Configuration
public class DebugClientConfig {

    @Bean
    public FilterRegistrationBean<HttpDebugInterceptor> httpDebugInterceptor(DebugClient debugClient, DebugClientProperties clientProperties) {
        FilterRegistrationBean<HttpDebugInterceptor> registrationBean =
                new FilterRegistrationBean<>(new HttpDebugInterceptor(debugClient, clientProperties));
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}
