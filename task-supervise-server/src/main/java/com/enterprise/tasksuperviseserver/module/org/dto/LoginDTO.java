package com.enterprise.tasksuperviseserver.module.org.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求参数 DTO
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
