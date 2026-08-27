package com.enterprise.tasksuperviseserver.module.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.feedback.entity.ProgressFeedback;
import com.enterprise.tasksuperviseserver.module.feedback.mapper.ProgressFeedbackMapper;
import com.enterprise.tasksuperviseserver.module.feedback.service.ProgressFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 进度反馈 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class ProgressFeedbackServiceImpl implements ProgressFeedbackService {

    private final ProgressFeedbackMapper progressFeedbackMapper;

    @Override
    public Page<ProgressFeedback> page(int pageNo, int pageSize) {
        Page<ProgressFeedback> page = Page.of(pageNo, pageSize);
        LambdaQueryWrapper<ProgressFeedback> wrapper = new LambdaQueryWrapper<ProgressFeedback>()
                .orderByDesc(ProgressFeedback::getFeedbackTime);
        return progressFeedbackMapper.selectPage(page, wrapper);
    }

    @Override
    public ProgressFeedback getDetail(Long feedbackId) {
        ProgressFeedback feedback = progressFeedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException(404, "进度反馈不存在");
        }
        return feedback;
    }

    @Override
    public ProgressFeedback add(ProgressFeedback feedback) {
        feedback.setUserId(UserContext.getUserId());
        feedback.setFeedbackTime(LocalDateTime.now());
        progressFeedbackMapper.insert(feedback);
        return feedback;
    }

    @Override
    public ProgressFeedback update(ProgressFeedback feedback) {
        if (feedback.getFeedbackId() == null) {
            throw new BusinessException(400, "反馈ID不能为空");
        }
        ProgressFeedback existing = progressFeedbackMapper.selectById(feedback.getFeedbackId());
        if (existing == null) {
            throw new BusinessException(404, "进度反馈不存在");
        }
        progressFeedbackMapper.updateById(feedback);
        return progressFeedbackMapper.selectById(feedback.getFeedbackId());
    }

    @Override
    public void delete(Long feedbackId) {
        ProgressFeedback existing = progressFeedbackMapper.selectById(feedbackId);
        if (existing == null) {
            throw new BusinessException(404, "进度反馈不存在");
        }
        progressFeedbackMapper.deleteById(feedbackId);
    }

    @Override
    public List<ProgressFeedback> listByTaskId(Long taskId) {
        LambdaQueryWrapper<ProgressFeedback> wrapper = new LambdaQueryWrapper<ProgressFeedback>()
                .eq(ProgressFeedback::getTaskId, taskId)
                .orderByDesc(ProgressFeedback::getFeedbackTime);
        return progressFeedbackMapper.selectList(wrapper);
    }
}
