package com.logiagent.auth;

import com.logiagent.common.auth.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@MapperScan("com.logiagent.auth.mapper")
@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class LogisticsAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsAuthServiceApplication.class, args);
    }
}
