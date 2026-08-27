package com.enterprise.tasksuperviseserver.module.org.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.org.entity.TaskGroup;

/**
 * 任务分组 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface TaskGroupService {

    /**
     * 分页查询分组列表
     */
    Page<TaskGroup> page(int page, int pageSize, String keyword);

    /**
     * 查询分组详情
     */
    TaskGroup detail(Long groupId);

    /**
     * 新增分组
     */
    TaskGroup create(TaskGroup taskGroup);

    /**
     * 更新分组
     */
    TaskGroup update(TaskGroup taskGroup);

    /**
     * 物理删除分组
     */
    boolean delete(Long groupId);
}
