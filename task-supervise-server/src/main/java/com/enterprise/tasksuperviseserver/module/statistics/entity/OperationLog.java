package com.enterprise.tasksuperviseserver.module.statistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 * <p>
 * 完整记录系统内所有关键操作，encryptedContent 存储 detail 的 SHA-256 哈希值实现防篡改。
 *
 * @author grq
 * @date 2026-08-26
 * @version v2.0.0
 */
@Data
@Accessors(chain = true)
@TableName("operation_log")
public class OperationLog {

    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /** 模块: task/feedback/acceptance/warn/file */
    @TableField("module")
    private String module;

    /** 操作类型: CREATE/UPDATE/DELETE/VERIFY/UPLOAD/REJECT */
    private String action;

    /** 关联任务ID */
    @TableField("task_id")
    private Long taskId;

    /** 操作人ID */
    @TableField("operator_id")
    private Long operatorId;

    /** 操作人姓名 */
    @TableField("operator_name")
    private String operatorName;

    /** 操作人部门ID */
    @TableField("dept_id")
    private Long deptId;

    /** 操作详情（明文描述） */
    private String detail;

    /** 操作详情 SHA-256 哈希值（防篡改） */
    @TableField("encrypted_content")
    private String encryptedContent;

    /** 操作人IP */
    private String ip;

    /** 操作时间 */
    @TableField("operate_time")
    private LocalDateTime operateTime;

}