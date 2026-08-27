package com.enterprise.tasksuperviseserver.module.warn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 预警记录实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("warn_record")
public class WarnRecord {

    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    private Long taskId;

    private Long ruleId;

    private Integer level;

    private String warnContent;

    private LocalDateTime pushTime;

}
