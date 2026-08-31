package com.enterprise.tasksuperviseserver.module.feedback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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

    /** 关联任务ID */
    @TableField("task_id")
    private Long taskId;

    /** 关联反馈ID */
    @TableField("feedback_id")
    private Long feedbackId;

    /** 原始文件名 */
    @TableField("original_name")
    private String originalName;

    /** 存储文件名 */
    @TableField("stored_name")
    private String storedName;

    /** 文件路径 */
    @TableField("file_path")
    private String filePath;

    /** 文件大小(字节) */
    @TableField("file_size")
    private Long fileSize;

    /** 文件类型扩展名 */
    @TableField("file_type")
    private String fileType;

    /** 上传人ID */
    @TableField("uploader_id")
    private Long uploaderId;

    /** 上传人姓名 */
    @TableField("uploader_name")
    private String uploaderName;

    /** 文件哈希(防篡改) */
    @TableField("encrypt_hash")
    private String encryptHash;

    /** 上传时间 */
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

}
