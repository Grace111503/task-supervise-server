package com.enterprise.tasksuperviseserver.module.org.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录接口返回数据 VO
 * 结构: { status, admin, token }
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultVO {

    /** 业务状态，200 为成功，400 为失败 */
    private Integer status;

    /** 失败时的错误消息 */
    private String message;

    /** 当前用户信息 */
    private LoginAdminVO admin;

    /** Token 信息 */
    private LoginTokenVO token;
}
