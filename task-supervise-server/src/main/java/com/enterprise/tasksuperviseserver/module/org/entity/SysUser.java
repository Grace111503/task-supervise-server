package com.enterprise.tasksuperviseserver.module.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 * <p>
 * 三级权限体系: user-普通执行人员 / manager-部门主管 / admin-督办管理员
 *
 * @author grq
 * @date 2026-08-26
 * @version v2.0.0
 */
@Data
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Long userId;

    @TableField(value = "username")
    private String userName;

    private String password;

    @TableField(value = "name")
    private String name;

    private String email;

    private String phone;

    private String avatar;

    /** 所属部门ID */
    @TableField(value = "dept_id")
    private Long deptId;

    /** 职位 */
    private String position;

    /** 角色: user-普通执行人员/manager-部门主管/admin-督办管理员 */
    @TableField(value = "role")
    private String roleCode;

    @TableLogic
    private Integer deleted;

    @TableField(value = "created_at")
    private LocalDateTime createTime;

    @TableField(value = "updated_at")
    private LocalDateTime updateTime;

}
