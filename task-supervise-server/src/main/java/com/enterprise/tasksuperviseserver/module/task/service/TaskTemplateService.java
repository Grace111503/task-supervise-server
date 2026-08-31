package com.enterprise.tasksuperviseserver.module.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskTemplate;

import java.util.List;

/**
 * 任务模板 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface TaskTemplateService {

    /**
     * 分页查询模板列表
     * 支持 templateName 模糊搜索 + templateType 筛选
     */
    Page<TaskTemplate> page(long pageNo, long pageSize, String templateName, Integer templateType);

    /**
     * 获取模板详情
     */
    TaskTemplate getDetail(Long templateId);

    /**
     * 新增模板
     */
    TaskTemplate create(TaskTemplate template);

    /**
     * 更新模板
     */
    TaskTemplate update(TaskTemplate template);

    /**
     * 物理删除模板
     */
    boolean delete(Long templateId);

    /**
     * 启用/停用模板
     *
     * @param templateId 模板ID
     * @param status     状态: 1-启用 0-停用
     */
    void updateStatus(Long templateId, Integer status);

    /**
     * 获取启用的模板列表
     *
     * @param templateType 模板类型（可选）
     * @return 模板列表
     */
    List<TaskTemplate> listEnabled(Integer templateType);

    /**
     * 根据模板类型统计模板数量
     *
     * @param templateType 模板类型
     * @return 数量
     */
    long countByType(Integer templateType);
}
