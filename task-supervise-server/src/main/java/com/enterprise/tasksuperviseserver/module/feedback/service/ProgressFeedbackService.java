package com.enterprise.tasksuperviseserver.module.feedback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.feedback.entity.ProgressFeedback;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 按 taskId 查询反馈列表（含关联文件）
     */
    List<ProgressFeedback> listByTaskIdWithFiles(Long taskId);

    /**
     * 获取进度反馈详情（含关联文件）
     */
    ProgressFeedback getDetailWithFiles(Long feedbackId);

    /**
     * 添加反馈并关联文件
     *
     * @param feedback 反馈信息
     * @param fileIds  关联的文件ID列表
     * @return 创建的反馈
     */
    ProgressFeedback addWithFiles(ProgressFeedback feedback, List<Long> fileIds);

    /**
     * 获取任务的最新反馈阶段号
     */
    Integer getNextStage(Long taskId);

    /**
     * 一步提交反馈 + 上传文件（multipart 方式）
     *
     * @param feedback 反馈信息
     * @param files    上传的文件数组（可为 null）
     * @return 创建的反馈（含关联文件）
     */
    ProgressFeedback addWithFilesMultipart(ProgressFeedback feedback, MultipartFile[] files);
}
