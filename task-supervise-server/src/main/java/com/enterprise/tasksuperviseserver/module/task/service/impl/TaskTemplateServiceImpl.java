package com.enterprise.tasksuperviseserver.module.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskTemplate;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskTemplateMapper;
import com.enterprise.tasksuperviseserver.module.task.service.TaskTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 任务模板 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class TaskTemplateServiceImpl implements TaskTemplateService {

    private final TaskTemplateMapper taskTemplateMapper;

    @Override
    public Page<TaskTemplate> page(long pageNo, long pageSize, String templateName, Integer templateType) {
        LambdaQueryWrapper<TaskTemplate> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(templateName)) {
            wrapper.like(TaskTemplate::getTemplateName, templateName);
        }
        if (templateType != null) {
            wrapper.eq(TaskTemplate::getTemplateType, templateType);
        }
        wrapper.orderByDesc(TaskTemplate::getCreateTime);
        return taskTemplateMapper.selectPage(Page.of(pageNo, pageSize), wrapper);
    }

    @Override
    public TaskTemplate getDetail(Long templateId) {
        TaskTemplate template = taskTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(404, "任务模板不存在");
        }
        return template;
    }

    @Override
    public TaskTemplate create(TaskTemplate template) {
        template.setCreateTime(LocalDateTime.now());
        taskTemplateMapper.insert(template);
        return template;
    }

    @Override
    public TaskTemplate update(TaskTemplate template) {
        if (template.getTemplateId() == null) {
            throw new BusinessException("任务模板ID不能为空");
        }
        TaskTemplate existing = taskTemplateMapper.selectById(template.getTemplateId());
        if (existing == null) {
            throw new BusinessException(404, "任务模板不存在");
        }
        taskTemplateMapper.updateById(template);
        return taskTemplateMapper.selectById(template.getTemplateId());
    }

    @Override
    public boolean delete(Long templateId) {
        TaskTemplate existing = taskTemplateMapper.selectById(templateId);
        if (existing == null) {
            throw new BusinessException(404, "任务模板不存在");
        }
        return taskTemplateMapper.deleteById(templateId) > 0;
    }
}
