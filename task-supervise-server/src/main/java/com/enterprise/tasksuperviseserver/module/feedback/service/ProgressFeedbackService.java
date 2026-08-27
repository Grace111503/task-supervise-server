package com.enterprise.tasksuperviseserver.module.feedback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.feedback.entity.ProgressFeedback;

import java.util.List;

/**
 * 进度反馈 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface ProgressFeedbackService {

    /**
     * 分页查询进度反馈列表
     */
    Page<ProgressFeedback> page(int pageNo, int pageSize);

    /**
     * 获取进度反馈详情
     */
    ProgressFeedback getDetail(Long feedbackId);

    /**
     * 新增进度反馈 (userId 取当前登录用户, feedbackTime 取当前时间)
     */
    ProgressFeedback add(ProgressFeedback feedback);

    /**
     * 更新进度反馈
     */
    ProgressFeedback update(ProgressFeedback feedback);

    /**
     * 物理删除进度反馈
     */
    void delete(Long feedbackId);

    /**
     * 按 taskId 查询反馈列表
     */
    List<ProgressFeedback> listByTaskId(Long taskId);
}
