package com.enterprise.tasksuperviseserver.module.acceptance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 验收实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("acceptance")
public class Acceptance {

    @TableId(value = "accept_id", type = IdType.AUTO)
    private Long acceptId;

    private Long taskId;

    private Long applicantId;

    private Long acceptorId;

    private Integer result;

    private String opinion;

    private LocalDateTime applyTime;

    private LocalDateTime acceptTime;

}
