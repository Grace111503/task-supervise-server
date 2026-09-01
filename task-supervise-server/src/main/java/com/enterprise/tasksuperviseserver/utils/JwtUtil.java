package com.enterprise.tasksuperviseserver.utils;

import com.enterprise.tasksuperviseserver.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    /** 声明中的用户ID字段 */
    public static final String CLAIM_USER_ID = "uid";
    /** 声明中的用户名字段 */
    public static final String CLAIM_USERNAME = "username";
    /** 声明中的真实姓名字段 */
    public static final String CLAIM_NAME = "name";
    /** 声明中的角色字段 */
    public static final String CLAIM_ROLE = "role";
    /** token 类型: access / refresh */
    public static final String CLAIM_TYPE = "type";

    /**
     * 获取签名 Key
     */
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Access Token
     */
    public String generateAccessToken(Long userId, String username, String name, String role) {
        return buildToken(userId, username, name, role, "access", jwtProperties.getAccessExpire());
    }

    /**
     * 生成 Refresh Token
     */
    public String generateRefreshToken(Long userId, String username, String name, String role) {
        return buildToken(userId, username, name, role, "refresh", jwtProperties.getRefreshExpire());
    }

    /**
     * 构建 Token
     */
    private String buildToken(Long userId, String username, String name, String role, String type, long expireSeconds) {
        Map<String, Object> claims = new HashMap<>(5);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_NAME, name);
        claims.put(CLAIM_ROLE, role == null ? "user" : role);
        claims.put(CLAIM_TYPE, type);

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireSeconds * 1000);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSignKey())
                .compact();
    }

    /**
     * 解析 Token 获取 Claims
     * @return 解析失败返回 null
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 校验 Token 是否有效 (非空 + 未过期 + 类型正确)
     * 兼容旧版 token（没有 type 字段时视为 access token）
     */
    public boolean validateToken(String token, String expectedType) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return false;
        }
        if (claims.getExpiration().before(new Date())) {
            return false;
        }
        if (expectedType != null) {
            String type = claims.get(CLAIM_TYPE, String.class);
            // 兼容旧版 token：如果没有 type 字段，默认视为 access token
            if (type == null && "access".equalsIgnoreCase(expectedType)) {
                return true;
            }
            if (!expectedType.equalsIgnoreCase(type)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从 Access Token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        Object uid = claims.get(CLAIM_USER_ID);
        if (uid == null) return null;
        return Long.valueOf(uid.toString());
    }

    /**
     * 获取 access_expire 秒数
     */
    public Long getAccessExpireSeconds() {
        return jwtProperties.getAccessExpire();
    }
}
