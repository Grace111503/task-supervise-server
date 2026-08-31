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

    /**
     * 批量添加指派人
     *
     * @param taskId    任务ID
     * @param userIds   用户ID列表
     * @param assigneeType 指派类型: 1-主负责人 2-协助人
     * @return 添加的指派人列表
     */
    List<TaskAssignee> batchCreate(Long taskId, List<Long> userIds, Integer assigneeType);

    /**
     * 更新指派类型
     *
     * @param id           指派记录ID
     * @param assigneeType 指派类型: 1-主负责人 2-协助人
     */
    void updateAssigneeType(Long id, Integer assigneeType);

    /**
     * 按任务ID和指派类型查询指派人列表
     *
     * @param taskId       任务ID
     * @param assigneeType 指派类型
     * @return 指派人列表
     */
    List<TaskAssignee> listByTaskIdAndType(Long taskId, Integer assigneeType);
}
