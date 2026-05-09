package com.logiagent.waybill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.logiagent.waybill.mapper")
@SpringBootApplication
public class LogisticsWaybillServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsWaybillServiceApplication.class, args);
    }
}
