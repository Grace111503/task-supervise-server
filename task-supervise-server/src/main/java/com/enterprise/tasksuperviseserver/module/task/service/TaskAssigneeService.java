package com.enterprise.tasksuperviseserver.module.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;

import java.util.List;

/**
 * 任务指派人 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface TaskAssigneeService {

    /**
     * 分页查询指派人列表
     */
    Page<TaskAssignee> page(long pageNo, long pageSize, Long taskId, Long userId);

    /**
     * 获取指派人详情
     */
    TaskAssignee getDetail(Long id);

    /**
     * 新增指派人
     * 创建时设置 receiveTime = now
     */
    TaskAssignee create(TaskAssignee assignee);

    /**
     * 更新指派人
     */
    TaskAssignee update(TaskAssignee assignee);

    /**
     * 物理删除指派人
     */
    boolean delete(Long id);

    /**
     * 按 taskId 查询指派人列表
     */
    List<TaskAssignee> listByTaskId(Long taskId);
}
