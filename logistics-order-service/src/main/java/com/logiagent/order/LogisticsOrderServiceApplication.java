package com.logiagent.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.logiagent.order.mapper")
@EnableFeignClients(basePackages = "com.logiagent.api.client")
@SpringBootApplication
public class LogisticsOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsOrderServiceApplication.class, args);
    }
}
