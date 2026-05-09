package com.logiagent.track;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.logiagent.track.mapper")
@SpringBootApplication
public class LogisticsTrackServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsTrackServiceApplication.class, args);
    }
}
