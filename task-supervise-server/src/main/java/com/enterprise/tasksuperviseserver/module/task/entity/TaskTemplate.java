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

    /** 模板类型: 1-行政 2-项目 3-整改 4-会议 5-客户对接 */
    private Integer templateType;

    private String description;

    /** 默认任务内容 */
    @TableField("default_content")
    private String defaultContent;

    /** 默认优先级: 1-普通 2-重要 3-紧急 */
    @TableField("default_priority")
    private Integer defaultPriority;

    /** 标准反馈要求 */
    @TableField("standard_feedback_req")
    private String standardFeedbackReq;

    /** 任务组ID */
    @TableField("group_id")
    private Long groupId;

    /** 状态: 1-启用 0-停用 */
    private Integer status;

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

}
