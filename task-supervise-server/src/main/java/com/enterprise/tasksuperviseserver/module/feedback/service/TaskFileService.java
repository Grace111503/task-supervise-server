package com.enterprise.tasksuperviseserver.module.feedback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;

import java.util.List;

/**
 * 任务文件 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface TaskFileService {

    /**
     * 分页查询任务文件列表
     */
    Page<TaskFile> page(int pageNo, int pageSize);

    /**
     * 获取任务文件详情
     */
    TaskFile getDetail(Long fileId);

    /**
     * 新增任务文件 (uploadTime 取当前时间)
     */
    TaskFile add(TaskFile taskFile);

    /**
     * 更新任务文件
     */
    TaskFile update(TaskFile taskFile);

    /**
     * 物理删除任务文件
     */
    void delete(Long fileId);

    /**
     * 按 taskId 查询文件列表
     */
    List<TaskFile> listByTaskId(Long taskId);

    /**
     * 按 feedbackId 查询文件
     */
    List<TaskFile> listByFeedbackId(Long feedbackId);

    /**
     * 绑定文件到反馈
     */
    void bindToFeedback(Long fileId, Long feedbackId);

    /**
     * 软删除文件（仅管理员）
     */
    void softDelete(Long fileId);
}
