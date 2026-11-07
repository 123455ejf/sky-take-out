package com.sky;

import com.sky.common.properties.JwtProperties;
import com.sky.common.properties.MinioProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.sky.mapper")
@EnableConfigurationProperties({JwtProperties.class, MinioProperties.class})
public class SkyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
    }
}
