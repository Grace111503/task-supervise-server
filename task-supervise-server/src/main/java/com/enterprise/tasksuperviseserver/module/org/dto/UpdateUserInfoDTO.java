package com.enterprise.tasksuperviseserver.module.org.dto;

import lombok.Data;

/**
 * 更新用户信息请求 DTO
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
public class UpdateUserInfoDTO {

    private String name;

    private String email;

    private String phone;

    private String avatar;
}
