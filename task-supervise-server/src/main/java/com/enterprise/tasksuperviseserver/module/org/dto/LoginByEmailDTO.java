package com.enterprise.tasksuperviseserver.module.org.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮箱登录请求参数 DTO
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
public class LoginByEmailDTO {

    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;
}
