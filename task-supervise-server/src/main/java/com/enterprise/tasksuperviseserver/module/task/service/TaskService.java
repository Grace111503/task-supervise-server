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
     * 指派任务给指定用户（单人模式）
     */
    void assign(Long taskId, Long assigneeId);

    /**
     * 批量分派任务给指定用户
     */
    void batchAssign(List<Long> taskIds, Long assigneeId);

    /**
     * 多人协办模式分派任务
     *
     * @param taskId     任务ID
     * @param assigneeIds 指派人ID列表
     * @param primaryId  主负责人ID（可选，不传则默认第一人为主负责人）
     */
    void assignMulti(Long taskId, List<Long> assigneeIds, Long primaryId);

    /**
     * 更新任务状态（整数状态码）
     */
    void updateStatus(Long taskId, Integer status);

    /**
     * 更新任务状态（字符串状态）
     */
    void updateStatus(Long taskId, String status);

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
     * 批量分派任务（返回结果）
     *
     * @param taskIds    任务ID列表
     * @param assigneeId 被指派人ID
     * @return 分派结果 { success, fail }
     */
    Map<String, Object> batchAssignResult(List<Long> taskIds, Long assigneeId);

    /**
     * 根据模板创建任务
     *
     * @param templateId 模板ID
     * @param task       任务基本信息（可覆盖模板默认值）
     * @return 创建的任务
     */
    Task createByTemplate(Long templateId, Task task);

    /**
     * 按分组查询任务列表
     *
     * @param groupId 任务组ID
     * @param page    页码
     * @param pageSize 每页数量
     * @return 任务列表
     */
    Map<String, Object> listByGroup(Long groupId, long page, long pageSize);

    /**
     * 驳回已完成的任务（管理员/主管）
     * 将任务状态从 completed 退回到 in_progress，并记录驳回原因
     *
     * @param taskId       任务ID
     * @param rejectRemark 驳回原因
     */
    void reject(Long taskId, String rejectRemark);

    /**
     * 验收任务（管理员/主管）
     * 通过：pending_accept → completed
     * 驳回：pending_accept → in_progress
     *
     * @param taskId       任务ID
     * @param acceptResult 验收结果（1通过 2驳回）
     * @param acceptRemark 验收意见
     */
    void accept(Long taskId, Integer acceptResult, String acceptRemark);

    /**
     * 获取任务全流程时间线
     *
     * @param taskId 任务ID
     * @return 时间线节点列表
     */
    List<Map<String, Object>> getTimeline(Long taskId);

    /**
     * 逾期任务清单（分页）
     */
    Map<String, Object> overdueList(long page, long pageSize, Long groupId, Long assigneeId);

    /**
     * 批量处置逾期任务
     *
     * @param taskIds 任务ID列表
     * @param action  操作：reassign(重新分派) / complete(标记完成) / extend(延长截止时间)
     * @param param   操作参数（assigneeId 或 newDeadline）
     */
    Map<String, Object> batchOverdueAction(List<Long> taskIds, String action, Map<String, Object> param);
}
