package io.terminus.debugger.client;

import io.terminus.debugger.client.core.DebugClient;
import io.terminus.debugger.client.core.DebuggerInstanceProvider;
import io.terminus.debugger.client.core.LocalDebugProperties;
import io.terminus.debugger.client.http.HttpDebugInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author stan
 * @date 2022/4/15
 */
@Configuration
@ComponentScan
@ConditionalOnProperty(prefix = "terminus.localdebug", value = "enable", havingValue = "true")
public class DebugClientConfig {

    @Bean
    public FilterRegistrationBean<HttpDebugInterceptor> httpDebugInterceptor(DebugClient debugClient, LocalDebugProperties debugProperties, DebuggerInstanceProvider instanceProvider) {
        FilterRegistrationBean<HttpDebugInterceptor> registrationBean =
                new FilterRegistrationBean<>(new HttpDebugInterceptor(debugClient, debugProperties, instanceProvider));
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}
