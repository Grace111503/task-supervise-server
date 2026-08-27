package com.enterprise.tasksuperviseserver.module.warn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 预警规则实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("warn_rule")
public class WarnRule {

    @TableId(value = "rule_id", type = IdType.AUTO)
    private Long ruleId;

    private String ruleName;

    private Integer level;

    private Integer beforeDays;

    private String pushFrequency;

    private String targetRoles;

    private Integer enabled;

    private LocalDateTime createTime;

}
