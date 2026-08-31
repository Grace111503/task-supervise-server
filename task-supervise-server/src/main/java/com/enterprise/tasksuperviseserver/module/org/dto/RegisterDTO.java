package com.enterprise.tasksuperviseserver.module.org.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求参数 DTO
 * <p>
 * 三级权限体系，注册时默认角色为普通执行人员(user)
 *
 * @author grq
 * @date 2026-08-27
 * @version v2.0.0
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名最多50字符")
    private String name;

    /** 所属部门ID，注册时必填 */
    private Long deptId;

    /** 职位 */
    @Size(max = 50, message = "职位最多50字符")
    private String position;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 20, message = "手机号最多20字符")
    private String phone;
}
