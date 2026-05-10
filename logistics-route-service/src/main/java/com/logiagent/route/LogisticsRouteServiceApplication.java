package com.logiagent.route;

import com.logiagent.route.config.BaiduMapProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@MapperScan("com.logiagent.route.mapper")
@EnableConfigurationProperties(BaiduMapProperties.class)
@SpringBootApplication
public class LogisticsRouteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsRouteServiceApplication.class, args);
    }
}
