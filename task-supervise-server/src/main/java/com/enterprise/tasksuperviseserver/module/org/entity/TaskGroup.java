package com.enterprise.tasksuperviseserver.module.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 任务分组实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("task_group")
public class TaskGroup {

    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    private String groupName;

    private Long deptId;

    private Integer groupType;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;

}
