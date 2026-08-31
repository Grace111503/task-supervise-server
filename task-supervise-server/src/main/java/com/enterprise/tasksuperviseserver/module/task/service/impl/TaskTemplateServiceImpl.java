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
import java.util.List;

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
        wrapper.orderByDesc(TaskTemplate::getCreatedAt);
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
        LocalDateTime now = LocalDateTime.now();
        template.setCreatedAt(now);
        template.setUpdatedAt(now);
        if (template.getStatus() == null) {
            template.setStatus(1); // 默认启用
        }
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
        template.setUpdatedAt(LocalDateTime.now());
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

    @Override
    public void updateStatus(Long templateId, Integer status) {
        TaskTemplate template = taskTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(404, "任务模板不存在");
        }
        template.setStatus(status);
        template.setUpdatedAt(LocalDateTime.now());
        taskTemplateMapper.updateById(template);
    }

    @Override
    public List<TaskTemplate> listEnabled(Integer templateType) {
        LambdaQueryWrapper<TaskTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskTemplate::getStatus, 1);
        if (templateType != null) {
            wrapper.eq(TaskTemplate::getTemplateType, templateType);
        }
        wrapper.orderByDesc(TaskTemplate::getCreatedAt);
        return taskTemplateMapper.selectList(wrapper);
    }

    @Override
    public long countByType(Integer templateType) {
        LambdaQueryWrapper<TaskTemplate> wrapper = new LambdaQueryWrapper<>();
        if (templateType != null) {
            wrapper.eq(TaskTemplate::getTemplateType, templateType);
        }
        return taskTemplateMapper.selectCount(wrapper);
    }
}
