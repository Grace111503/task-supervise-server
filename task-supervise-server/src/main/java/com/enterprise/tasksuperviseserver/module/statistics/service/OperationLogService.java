package com.enterprise.tasksuperviseserver.module.statistics.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.statistics.entity.OperationLog;

/**
 * 操作日志 Service
 */
public interface OperationLogService {

    /**
     * 分页查询操作日志列表（支持 module 筛选 + taskId 筛选）
     */
    Page<OperationLog> page(int pageNo, int pageSize, String module, Long taskId);

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
}
