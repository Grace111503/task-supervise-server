package com.enterprise.tasksuperviseserver.module.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 系统角色实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("sys_role")
public class SysRole {

    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    private String roleCode;

    private String roleName;

    private Integer dataScope;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;

}
