package com.enterprise.tasksuperviseserver.module.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 任务实体
 * <p>
 * 映射表 task: id, title, description, status, priority, deadline, remark, attachments,
 * creator_id, creator_name, assignee_id, assignee_name, deleted, created_at, updated_at
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("task")
public class Task {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    /** 状态: pending/in_progress/completed/overdue */
    private String status;

    /** 优先级: high/medium/low */
    private String priority;

    /** 所属部门ID，用于权限过滤 */
    @TableField("dept_id")
    private Long deptId;

    /** 关联模板ID */
    @TableField("template_id")
    private Long templateId;

    /** 分派模式: 1-单人 2-多人协办 */
    @TableField("assignee_mode")
    private Integer assigneeMode;

    /** 任务组ID */
    @TableField("group_id")
    private Long groupId;

    private LocalDateTime deadline;

    private String remark;

    /** 驳回原因（管理员/主管驳回时填写） */
    @TableField("reject_remark")
    private String rejectRemark;

    /** 驳回时间 */
    @TableField("rejected_at")
    private LocalDateTime rejectedAt;

    /** 验收结果: 0待验收 1通过 2驳回 */
    @TableField("accept_result")
    private Integer acceptResult;

    /** 验收意见 */
    @TableField("accept_remark")
    private String acceptRemark;

    /** 验收时间 */
    @TableField("accepted_at")
    private LocalDateTime acceptedAt;

    /** 验收人ID */
    @TableField("accepted_by")
    private Long acceptedBy;

    private String attachments;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("creator_name")
    private String creatorName;

    @TableField("assignee_id")
    private Long assigneeId;

    @TableField("assignee_name")
    private String assigneeName;

    @TableLogic
    private Integer deleted;

    /** 逾期标记: 0-未标记 1-已标记（防止重复触发逾期提醒） */
    @TableField("overdue_marked")
    private Integer overdueMarked;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

}
