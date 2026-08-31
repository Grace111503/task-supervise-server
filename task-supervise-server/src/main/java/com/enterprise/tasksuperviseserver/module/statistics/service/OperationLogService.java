package com.enterprise.tasksuperviseserver.module.statistics.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.statistics.entity.OperationLog;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作日志 Service
 */
public interface OperationLogService {

    /**
     * 分页查询操作日志列表
     *
     * @param pageNo     页码
     * @param pageSize   每页大小
     * @param module     模块筛选（可为 null）
     * @param taskId     任务ID筛选（可为 null）
     * @param operatorId 操作人ID筛选（可为 null）
     * @param deptId     部门ID筛选（可为 null）
     * @param startTime  开始时间（可为 null）
     * @param endTime    结束时间（可为 null）
     */
    Page<OperationLog> page(int pageNo, int pageSize, String module, Long taskId,
                            Long operatorId, Long deptId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取操作日志详情
     */
    OperationLog getById(Long logId);

    /**
     * 新增操作日志
     * operatorId = UserContext.getUserId(), operatorName = UserContext.getUsername(), operateTime = now
     */
    OperationLog create(OperationLog entity);

    /**
     * 更新操作日志
     */
    OperationLog update(OperationLog entity);

    /**
     * 物理删除操作日志
     */
    void delete(Long logId);

    /**
     * 验证日志哈希完整性
     *
     * @param logId 日志ID
     * @return 验证结果: valid=true/false, storedHash, computedHash
     */
    Map<String, Object> verify(Long logId);
}
