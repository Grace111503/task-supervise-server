package com.enterprise.tasksuperviseserver.module.org.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功响应中的用户信息 VO
 * <p>
 * 三级权限体系: user-普通执行人员 / manager-部门主管 / admin-督办管理员
 *
 * @author grq
 * @date 2026-08-26
 * @version v2.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAdminVO {

    private Long id;

    private String name;

    private String avatar;

    /** 角色代码: user/manager/admin */
    private String role;

    /** 角色描述（中文） */
    private String roleDesc;

    /** 所属部门ID */
    private Long deptId;

    /** 职位 */
    private String position;
}
