package io.terminus.debugger.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author stan
 * @date 2022/5/9
 */
@RestController
@Slf4j
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World", required = false) String name) {
        log.info("hello world {}", System.currentTimeMillis());
        return String.format("Hello %s!", name);
    }
}
