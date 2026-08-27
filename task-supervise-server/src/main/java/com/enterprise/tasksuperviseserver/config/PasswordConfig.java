package com.enterprise.tasksuperviseserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置
 * 使用 Spring Security 的 BCrypt (避免引入完整 spring-security 依赖)
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
