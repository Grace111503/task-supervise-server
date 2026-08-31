package com.enterprise.tasksuperviseserver.module.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 任务指派人实体
 * <p>
 * 映射表 task_assignee: assignee_id, task_id, user_id, assignee_name, assignee_type, status, created_at
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("task_assignee")
public class TaskAssignee {

    @TableId(value = "assignee_id", type = IdType.AUTO)
    private Long assigneeId;

    private Long taskId;

    private Long userId;

    @TableField("assignee_name")
    private String assigneeName;

    /** 指派类型: 1-主负责人 2-协助人 */
    @TableField("assignee_type")
    private Integer assigneeType;

    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;

}
