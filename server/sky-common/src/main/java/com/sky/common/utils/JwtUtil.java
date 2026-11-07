package com.sky.common.utils;

import com.sky.common.constant.JwtClaimsConstant;
import com.sky.common.constant.RoleConstant;
import com.sky.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String createAdminToken(Long employeeId) {
        return createToken(employeeId, RoleConstant.ADMIN,
                jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl());
    }

    public String createUserToken(Long userId) {
        return createToken(userId, RoleConstant.USER,
                jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl());
    }

    public Claims parseAdminToken(String token) {
        return parseToken(token, RoleConstant.ADMIN, jwtProperties.getAdminSecretKey());
    }

    public Claims parseUserToken(String token) {
        return parseToken(token, RoleConstant.USER, jwtProperties.getUserSecretKey());
    }

    public long getAdminTtl() {
        return jwtProperties.getAdminTtl();
    }

    public long getUserTtl() {
        return jwtProperties.getUserTtl();
    }

    private String createToken(Long userId, String role, String secretKey, long ttl) {
        Date now = new Date();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, userId);
        claims.put(JwtClaimsConstant.ROLE, role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ttl))
                .signWith(buildKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseToken(String token, String expectedRole, String secretKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(buildKey(secretKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            if (!expectedRole.equals(claims.get(JwtClaimsConstant.ROLE, String.class))) {
                return null;
            }
            return claims;
        } catch (JwtException | IllegalArgumentException exception) {
            return null;
        }
    }

    private Key buildKey(String secretKey) {
        if (secretKey == null || secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT 密钥长度至少需要 32 字节");
        }
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
