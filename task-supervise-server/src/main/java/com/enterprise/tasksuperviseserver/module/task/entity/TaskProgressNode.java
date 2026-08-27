package com.enterprise.tasksuperviseserver.module.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDate;

/**
 * 任务进度节点实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("task_progress_node")
public class TaskProgressNode {

    @TableId(value = "node_id", type = IdType.AUTO)
    private Long nodeId;

    private Long taskId;

    private String nodeName;

    private Integer stage;

    private LocalDate planDate;

    private Integer status;

}
