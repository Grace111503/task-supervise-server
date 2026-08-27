package com.enterprise.tasksuperviseserver.module.feedback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 进度反馈实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("progress_feedback")
public class ProgressFeedback {

    @TableId(value = "feedback_id", type = IdType.AUTO)
    private Long feedbackId;

    private Long taskId;

    private Long userId;

    private String completedContent;

    private String nextPlan;

    private Integer stage;

    private LocalDateTime feedbackTime;

}
