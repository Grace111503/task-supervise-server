package com.enterprise.tasksuperviseserver.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.statistics.entity.OperationLog;
import com.enterprise.tasksuperviseserver.module.statistics.mapper.OperationLogMapper;
import com.enterprise.tasksuperviseserver.module.statistics.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 操作日志 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public Page<OperationLog> page(int pageNo, int pageSize, String module, Long taskId,
                                   Long operatorId, Long deptId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(module)) {
            wrapper.eq(OperationLog::getModule, module);
        }
        if (taskId != null) {
            wrapper.eq(OperationLog::getTaskId, taskId);
        }
        if (operatorId != null) {
            wrapper.eq(OperationLog::getOperatorId, operatorId);
        }
        if (deptId != null) {
            wrapper.eq(OperationLog::getDeptId, deptId);
        }
        if (startTime != null) {
            wrapper.ge(OperationLog::getOperateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(OperationLog::getOperateTime, endTime);
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
        entity.setOperatorName(UserContext.getName() != null ? UserContext.getName() : UserContext.getUsername());
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

    @Override
    public Map<String, Object> verify(Long logId) {
        OperationLog operationLog = operationLogMapper.selectById(logId);
        if (operationLog == null) {
            throw new BusinessException(404, "操作日志不存在");
        }

        String storedHash = operationLog.getEncryptedContent();
        String detail = operationLog.getDetail();

        // 重新计算 SHA-256
        String computedHash = null;
        if (detail != null) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(detail.getBytes(StandardCharsets.UTF_8));
                computedHash = HexFormat.of().formatHex(hashBytes);
            } catch (Exception e) {
                log.warn("SHA-256 计算失败: {}", e.getMessage());
            }
        }

        boolean valid = storedHash != null && storedHash.equals(computedHash);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("logId", logId);
        result.put("valid", valid);
        result.put("storedHash", storedHash);
        result.put("computedHash", computedHash);
        return result;
    }
}
