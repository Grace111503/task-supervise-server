package com.enterprise.tasksuperviseserver.module.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 系统部门实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("sys_dept")
public class SysDept {

    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;

    private Long parentId;

    private String deptName;

    private Integer sort;

    private String leader;

    private String phone;

    private Integer status;

    @TableField(value = "created_at")
    private LocalDateTime createTime;

    @TableField(value = "updated_at")
    private LocalDateTime updateTime;

}
