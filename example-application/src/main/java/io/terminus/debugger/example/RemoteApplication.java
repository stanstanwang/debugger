package io.terminus.debugger.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author stan
 * @date 2022/4/19
 */
@SpringBootApplication
public class RemoteApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RemoteApplication.class)
                .profiles("remote")
                .run(args);
    }

}
