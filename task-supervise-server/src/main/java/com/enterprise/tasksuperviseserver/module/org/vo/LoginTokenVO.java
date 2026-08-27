package com.enterprise.tasksuperviseserver.module.org.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功响应中的 Token VO
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginTokenVO {

    /** JWT Access Token */
    private String access_token;

    /** Refresh Token */
    private String refresh_token;

    /** Access Token 有效时长(秒) */
    private Long expires_in;
}
