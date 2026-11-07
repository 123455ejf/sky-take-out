package com.sky.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sky.jwt")
public class JwtProperties {

    private String adminSecretKey;
    private String userSecretKey;
    private long adminTtl = 7_200_000L;
    private long userTtl = 7_200_000L;
}
