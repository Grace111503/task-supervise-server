package com.enterprise.tasksuperviseserver.module.task.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务列表项 VO（含计算字段）
 *
 * @author grq
 * @date 2026-08-28
 * @version v1.0.0
 */
@Data
public class TaskListItemVO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long deptId;
    private Long templateId;
    private Integer assigneeMode;
    private Long groupId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    private String remark;
    private String rejectRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rejectedAt;

    private Integer acceptResult;
    private String acceptRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptedAt;

    private Long creatorId;
    private String creatorName;
    private Long assigneeId;
    private String assigneeName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ===== 计算字段 =====

    /** 多人协办模式下的执行人ID列表 */
    private java.util.List<Long> multiAssigneeIds;

    /** 剩余天数（负数表示已逾期） */
    private Long remainingDays;

    /** 逾期天数（仅逾期任务有值） */
    private Long overdueDays;

    /** 反馈次数 */
    private Integer feedbackCount;
}