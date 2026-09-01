package com.enterprise.tasksuperviseserver.module.warn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 站内消息实体
 * <p>
 * 映射表 in_app_message: msg_id, user_id, title, content, level, read_status, created_at
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("in_app_message")
public class InAppMessage {

    @TableId(value = "msg_id", type = IdType.AUTO)
    private Long msgId;

    private Long userId;

    private String title;

    private String content;

    /** 消息级别 1-普通 2-重要 3-紧急 */
    private Integer level;

    /** 消息类型 TASK-任务通知 WARN-预警 ACCEPT-验收通知 */
    @TableField("msg_type")
    private String msgType;

    /** 关联任务ID，用于点击跳转 */
    @TableField("related_id")
    private Long relatedId;

    /** 0-未读 1-已读 */
    @TableField("read_status")
    private Integer readStatus;

    @TableField("created_at")
    private LocalDateTime createdAt;

}
