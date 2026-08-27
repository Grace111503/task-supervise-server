package com.enterprise.tasksuperviseserver.module.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskTemplateField;

import java.util.List;

/**
 * 任务模板字段 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface TaskTemplateFieldService {

    /**
     * 分页查询模板字段列表
     * 支持 templateId 筛选 + fieldName 模糊搜索
     */
    Page<TaskTemplateField> page(long pageNo, long pageSize, Long templateId, String fieldName);

    /**
     * 获取字段详情
     */
    TaskTemplateField getDetail(Long fieldId);

    /**
     * 新增字段
     */
    TaskTemplateField create(TaskTemplateField field);

    /**
     * 更新字段
     */
    TaskTemplateField update(TaskTemplateField field);

    /**
     * 物理删除字段
     */
    boolean delete(Long fieldId);

    /**
     * 按模板ID查询字段列表
     */
    List<TaskTemplateField> listByTemplateId(Long templateId);
}
