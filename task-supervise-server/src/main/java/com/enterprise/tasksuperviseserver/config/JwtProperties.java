package com.enterprise.tasksuperviseserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置属性
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 签名密钥 */
    private String secret;

    /** Access Token 有效期(秒) */
    private Long accessExpire = 7200L;

    /** Refresh Token 有效期(秒) */
    private Long refreshExpire = 604800L;
}
