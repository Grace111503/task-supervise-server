package com.enterprise.tasksuperviseserver.module.acceptance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 流程配置实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("flow_config")
public class FlowConfig {

    @TableId(value = "flow_id", type = IdType.AUTO)
    private Long flowId;

    private String flowName;

    private Long deptId;

    private String nodeConfig;

    private Integer status;

    private LocalDateTime createTime;

}
