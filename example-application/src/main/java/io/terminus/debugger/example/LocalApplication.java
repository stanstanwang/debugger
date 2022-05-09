package io.terminus.debugger.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author stan
 * @date 2022/4/19
 */
@SpringBootApplication
public class LocalApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LocalApplication.class)
                .profiles("local")
                .run(args);
    }

}
