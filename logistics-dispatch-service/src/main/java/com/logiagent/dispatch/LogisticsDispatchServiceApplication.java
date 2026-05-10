package com.logiagent.dispatch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.logiagent.dispatch.mapper")
@SpringBootApplication
public class LogisticsDispatchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsDispatchServiceApplication.class, args);
    }
}
