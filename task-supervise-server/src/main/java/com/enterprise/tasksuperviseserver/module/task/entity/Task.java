package com.enterprise.tasksuperviseserver.module.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 任务实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("task")
public class Task {

    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    private Long templateId;

    private Long groupId;

    private String title;

    private String content;

    private Integer priority;

    private LocalDateTime deadline;

    private Long creatorId;

    private Integer assigneeMode;

    private Integer status;

    private Long flowConfigId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
