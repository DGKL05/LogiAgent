package com.logiagent.agent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.logiagent.agent.mapper")
@EnableFeignClients(basePackages = "com.logiagent.api.client")
@SpringBootApplication
public class LogisticsAiAgentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsAiAgentServiceApplication.class, args);
    }
}
