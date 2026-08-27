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
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("operation_log")
public class OperationLog {

    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @TableField("module")
    private String module;

    private String operation;

    private Long operatorId;

    private String operatorName;

    private Long taskId;

    private LocalDateTime operateTime;

    private String encryptedContent;

}
