package com.enterprise.tasksuperviseserver.module.statistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 统计报表实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("statistics_report")
public class StatisticsReport {

    @TableId(value = "report_id", type = IdType.AUTO)
    private Long reportId;

    private String period;

    private String periodValue;

    private Long deptId;

    private Long userId;

    private Integer totalDispatch;

    private BigDecimal onTimeRate;

    private Integer overdueCount;

    private BigDecimal avgCompleteDays;

    private LocalDateTime generateTime;

}
