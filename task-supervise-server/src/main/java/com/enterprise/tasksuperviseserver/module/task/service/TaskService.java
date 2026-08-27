package com.enterprise.tasksuperviseserver.module.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;

import java.util.List;
import java.util.Map;

/**
 * 任务 Service
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
public interface TaskService {

    /**
     * 分页查询任务列表
     * 支持 status/priority/groupId/assigneeId 筛选 + keyword 模糊搜索
     *
     * @return { list, page, pageSize, total }
     */
    Map<String, Object> list(long page, long pageSize, Integer status, Integer priority, Long groupId, String keyword, Long assigneeId);

    /**
     * 获取任务详情
     */
    Task getDetail(Long taskId);

    /**
     * 新增任务
     */
    Task create(Task task);

    /**
     * 更新任务
     */
    Task update(Task task);

    /**
     * 物理删除任务
     */
    boolean delete(Long taskId);

    /**
     * 指派任务给指定用户
     */
    void assign(Long taskId, Long assigneeId);

    /**
     * 更新任务状态
     */
    void updateStatus(Long taskId, Integer status);

    /**
     * 获取当前用户任务统计
     *
     * @return { total, pending, inProgress, completed, overdue }
     */
    Map<String, Object> statistics();

    /**
     * Excel 批量导入任务
     *
     * @param bytes Excel 文件内容
     * @return 导入结果 { success, fail, total }
     */
    Map<String, Object> batchImport(byte[] bytes);

    /**
     * 批量分派任务
     *
     * @param taskIds    任务ID列表
     * @param assigneeId 被指派人ID
     * @return 分派结果 { success, fail }
     */
    Map<String, Object> batchAssign(List<Long> taskIds, Long assigneeId);
}
