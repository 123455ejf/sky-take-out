package com.sky.common.utils;

import com.sky.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setAdminSecretKey("admin-test-secret-key-that-is-longer-than-32-bytes");
        properties.setUserSecretKey("user-test-secret-key-that-is-longer-than-32-bytes");
        properties.setAdminTtl(60_000L);
        properties.setUserTtl(60_000L);
        jwtUtil = new JwtUtil(properties);
    }

    @Test
    void shouldCreateAndParseAdminToken() {
        String token = jwtUtil.createAdminToken(10L);
        Claims claims = jwtUtil.parseAdminToken(token);

        assertNotNull(claims);
        assertEquals(10L, ((Number) claims.get("userId")).longValue());
        assertEquals("admin", claims.get("role"));
    }

    @Test
    void shouldRejectTokenFromAnotherRole() {
        String userToken = jwtUtil.createUserToken(20L);

        assertNull(jwtUtil.parseAdminToken(userToken));
    }
}
