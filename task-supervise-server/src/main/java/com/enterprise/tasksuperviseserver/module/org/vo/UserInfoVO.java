package com.enterprise.tasksuperviseserver.module.org.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息 VO
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {

    private Long id;

    private String name;

    private String avatar;

    private String email;

    private String phone;

    private String role;
}
