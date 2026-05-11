package com.logiagent.gateway;

import com.logiagent.common.auth.JwtProperties;
import com.logiagent.gateway.config.AuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({JwtProperties.class, AuthProperties.class})
@SpringBootApplication
public class LogisticsGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsGatewayApplication.class, args);
    }
}
