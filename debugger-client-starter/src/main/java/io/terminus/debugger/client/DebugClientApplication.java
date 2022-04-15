package io.terminus.debugger.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author stan
 * @date 2022/3/21
 */
@SpringBootApplication
// @EnableConfigurationProperties
public class DebugClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(DebugClientApplication.class, args);
    }

}
