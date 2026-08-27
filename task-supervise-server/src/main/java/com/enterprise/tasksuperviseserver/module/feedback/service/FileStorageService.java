package com.enterprise.tasksuperviseserver.module.feedback.service;

import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储 Service
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
public interface FileStorageService {

    /**
     * 上传文件并保存元数据
     *
     * @param file       MultipartFile
     * @param taskId     关联任务ID（可为 null）
     * @param feedbackId 关联反馈ID（可为 null）
     * @return TaskFile 文件元数据
     */
    TaskFile upload(MultipartFile file, Long taskId, Long feedbackId);

    /**
     * 获取文件资源（用于下载/预览）
     *
     * @param fileId 文件ID
     * @return FileResource 包含 Resource 和文件元数据
     */
    FileResource getFileResource(Long fileId);

    /**
     * 文件资源包装
     */
    record FileResource(Resource resource, TaskFile taskFile) {
    }
}
