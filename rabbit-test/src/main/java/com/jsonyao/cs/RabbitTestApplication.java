package com.jsonyao.cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * RabbitMQ测试工程启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.jsonyao.cs"})
public class RabbitTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitTestApplication.class, args);
    }
}
