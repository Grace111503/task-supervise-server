package com.enterprise.tasksuperviseserver.module.feedback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import java.util.List;

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

    /** 关联任务ID */
    private Long taskId;

    /** 反馈人ID */
    private Long userId;

    /** 反馈人姓名 */
    @TableField("user_name")
    private String userName;

    /** 当期完成内容 */
    @TableField("completed_content")
    private String completedContent;

    /** 下一步工作计划 */
    @TableField("next_plan")
    private String nextPlan;

    /** 进度百分比(0-100) */
    @TableField("progress_percent")
    private Integer progressPercent;

    /** 反馈阶段/轮次 */
    private Integer stage;

    /** 反馈时间 */
    @TableField("feedback_time")
    private LocalDateTime feedbackTime;

    /** 关联文件列表（非数据库字段） */
    @TableField(exist = false)
    private List<TaskFile> files;

}
