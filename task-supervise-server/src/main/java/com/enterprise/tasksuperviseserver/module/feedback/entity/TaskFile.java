package com.enterprise.tasksuperviseserver.module.feedback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 任务文件实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("task_file")
public class TaskFile {

    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    private Long taskId;

    private Long feedbackId;

    private String fileName;

    private String filePath;

    private String fileType;

    private Long fileSize;

    private String encryptHash;

    private LocalDateTime uploadTime;

}
