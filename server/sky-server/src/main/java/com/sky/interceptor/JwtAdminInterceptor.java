package com.sky.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.common.constant.JwtClaimsConstant;
import com.sky.common.context.BaseContext;
import com.sky.common.result.Result;
import com.sky.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAdminInterceptor implements HandlerInterceptor {

    private static final String BLACKLIST_PREFIX = "sky:token:blacklist:";

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            writeUnauthorized(response);
            return false;
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token))) {
            writeUnauthorized(response);
            return false;
        }

        Claims claims = jwtUtil.parseAdminToken(token);
        if (claims == null) {
            writeUnauthorized(response);
            return false;
        }

        Number employeeId = (Number) claims.get(JwtClaimsConstant.USER_ID);
        BaseContext.setCurrentId(employeeId.longValue());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception exception) {
        BaseContext.removeCurrentId();
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), Result.error(401, "未登录或 Token 已失效，请重新登录"));
    }
}
