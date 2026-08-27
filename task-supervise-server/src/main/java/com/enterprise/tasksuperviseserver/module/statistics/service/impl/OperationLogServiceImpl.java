package com.enterprise.tasksuperviseserver.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.statistics.entity.OperationLog;
import com.enterprise.tasksuperviseserver.module.statistics.mapper.OperationLogMapper;
import com.enterprise.tasksuperviseserver.module.statistics.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 操作日志 Service 实现
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public Page<OperationLog> page(int pageNo, int pageSize, String module, Long taskId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(module)) {
            wrapper.eq(OperationLog::getModule, module);
        }
        if (taskId != null) {
            wrapper.eq(OperationLog::getTaskId, taskId);
        }
        wrapper.orderByDesc(OperationLog::getOperateTime);
        return operationLogMapper.selectPage(Page.of(pageNo, pageSize), wrapper);
    }

    @Override
    public OperationLog getById(Long logId) {
        OperationLog log = operationLogMapper.selectById(logId);
        if (log == null) {
            throw new BusinessException(404, "操作日志不存在");
        }
        return log;
    }

    @Override
    public OperationLog create(OperationLog entity) {
        entity.setLogId(null);
        entity.setOperatorId(UserContext.getUserId());
        entity.setOperatorName(UserContext.getUsername());
        entity.setOperateTime(LocalDateTime.now());
        operationLogMapper.insert(entity);
        return entity;
    }

    @Override
    public OperationLog update(OperationLog entity) {
        if (entity.getLogId() == null) {
            throw new BusinessException("日志ID不能为空");
        }
        OperationLog exist = operationLogMapper.selectById(entity.getLogId());
        if (exist == null) {
            throw new BusinessException(404, "操作日志不存在");
        }
        operationLogMapper.updateById(entity);
        return operationLogMapper.selectById(entity.getLogId());
    }

    @Override
    public void delete(Long logId) {
        OperationLog exist = operationLogMapper.selectById(logId);
        if (exist == null) {
            throw new BusinessException(404, "操作日志不存在");
        }
        operationLogMapper.deleteById(logId);
    }
}
