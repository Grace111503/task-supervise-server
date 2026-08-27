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
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("in_app_message")
public class InAppMessage {

    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    private Long warnRecordId;

    private Long userId;

    private String title;

    private String content;

    private String msgType;

    @TableField("is_read")
    private Integer isRead;

    private LocalDateTime createTime;

}
