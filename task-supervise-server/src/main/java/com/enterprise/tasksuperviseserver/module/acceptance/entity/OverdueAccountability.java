package com.enterprise.tasksuperviseserver.module.acceptance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 逾期问责实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("overdue_accountability")
public class OverdueAccountability {

    @TableId(value = "accountability_id", type = IdType.AUTO)
    private Long accountabilityId;

    private Long taskId;

    private Integer overdueDays;

    private String reason;

    private String disposition;

    private LocalDateTime archiveTime;

}
