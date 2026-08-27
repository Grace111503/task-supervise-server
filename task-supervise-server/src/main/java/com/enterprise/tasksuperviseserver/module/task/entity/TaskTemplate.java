package com.enterprise.tasksuperviseserver.module.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 任务模板实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("task_template")
public class TaskTemplate {

    @TableId(value = "template_id", type = IdType.AUTO)
    private Long templateId;

    private String templateName;

    private Integer templateType;

    private String defaultContent;

    private Integer defaultPriority;

    private String standardFeedbackReq;

    private Integer status;

    private LocalDateTime createTime;

}
