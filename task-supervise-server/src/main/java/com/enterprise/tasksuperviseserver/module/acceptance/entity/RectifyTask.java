package com.enterprise.tasksuperviseserver.module.acceptance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 整改任务实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("rectify_task")
public class RectifyTask {

    @TableId(value = "rectify_id", type = IdType.AUTO)
    private Long rectifyId;

    private Long taskId;

    private Long acceptId;

    private String rectifyReason;

    private String rectifyOpinion;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime completeTime;

}
