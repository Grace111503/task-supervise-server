package com.enterprise.tasksuperviseserver.module.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskTemplateField;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskTemplateFieldMapper;
import com.enterprise.tasksuperviseserver.module.task.service.TaskTemplateFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 任务模板字段 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class TaskTemplateFieldServiceImpl implements TaskTemplateFieldService {

    private final TaskTemplateFieldMapper taskTemplateFieldMapper;

    @Override
    public Page<TaskTemplateField> page(long pageNo, long pageSize, Long templateId, String fieldName) {
        LambdaQueryWrapper<TaskTemplateField> wrapper = new LambdaQueryWrapper<>();
        if (templateId != null) {
            wrapper.eq(TaskTemplateField::getTemplateId, templateId);
        }
        if (StringUtils.hasText(fieldName)) {
            wrapper.like(TaskTemplateField::getFieldName, fieldName);
        }
        wrapper.orderByAsc(TaskTemplateField::getSort);
        return taskTemplateFieldMapper.selectPage(Page.of(pageNo, pageSize), wrapper);
    }

    @Override
    public TaskTemplateField getDetail(Long fieldId) {
        TaskTemplateField field = taskTemplateFieldMapper.selectById(fieldId);
        if (field == null) {
            throw new BusinessException(404, "模板字段不存在");
        }
        return field;
    }

    @Override
    public TaskTemplateField create(TaskTemplateField field) {
        if (field.getTemplateId() == null) {
            throw new BusinessException("模板ID不能为空");
        }
        taskTemplateFieldMapper.insert(field);
        return field;
    }

    @Override
    public TaskTemplateField update(TaskTemplateField field) {
        if (field.getFieldId() == null) {
            throw new BusinessException("字段ID不能为空");
        }
        TaskTemplateField existing = taskTemplateFieldMapper.selectById(field.getFieldId());
        if (existing == null) {
            throw new BusinessException(404, "模板字段不存在");
        }
        taskTemplateFieldMapper.updateById(field);
        return taskTemplateFieldMapper.selectById(field.getFieldId());
    }

    @Override
    public boolean delete(Long fieldId) {
        TaskTemplateField existing = taskTemplateFieldMapper.selectById(fieldId);
        if (existing == null) {
            throw new BusinessException(404, "模板字段不存在");
        }
        return taskTemplateFieldMapper.deleteById(fieldId) > 0;
    }

    @Override
    public List<TaskTemplateField> listByTemplateId(Long templateId) {
        return taskTemplateFieldMapper.selectList(new LambdaQueryWrapper<TaskTemplateField>()
                .eq(TaskTemplateField::getTemplateId, templateId)
                .orderByAsc(TaskTemplateField::getSort));
    }
}
