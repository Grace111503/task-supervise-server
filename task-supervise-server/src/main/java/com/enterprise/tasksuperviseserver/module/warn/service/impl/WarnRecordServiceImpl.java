package com.enterprise.tasksuperviseserver.module.warn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRecord;
import com.enterprise.tasksuperviseserver.module.warn.mapper.WarnRecordMapper;
import com.enterprise.tasksuperviseserver.module.warn.mq.WarnMessageProducer;
import com.enterprise.tasksuperviseserver.module.warn.service.WarnRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警记录 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class WarnRecordServiceImpl implements WarnRecordService {

    private final WarnRecordMapper warnRecordMapper;

    @Autowired(required = false)
    private WarnMessageProducer warnMessageProducer;

    @Override
    public Page<WarnRecord> page(int pageNo, int pageSize, Long taskId, Integer level) {
        LambdaQueryWrapper<WarnRecord> wrapper = new LambdaQueryWrapper<>();
        if (taskId != null) {
            wrapper.eq(WarnRecord::getTaskId, taskId);
        }
        if (level != null) {
            wrapper.eq(WarnRecord::getLevel, level);
        }
        wrapper.orderByDesc(WarnRecord::getPushTime);
        return warnRecordMapper.selectPage(Page.of(pageNo, pageSize), wrapper);
    }

    @Override
    public WarnRecord getById(Long recordId) {
        WarnRecord record = warnRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(404, "预警记录不存在");
        }
        return record;
    }

    @Override
    public WarnRecord create(WarnRecord entity) {
        entity.setRecordId(null);
        entity.setPushTime(LocalDateTime.now());
        warnRecordMapper.insert(entity);
        // 通过 RabbitMQ 异步推送站内消息（RabbitMQ 不可用时跳过）
        if (warnMessageProducer != null) {
            warnMessageProducer.send(entity);
        }
        return entity;
    }

    @Override
    public WarnRecord update(WarnRecord entity) {
        if (entity.getRecordId() == null) {
            throw new BusinessException("记录ID不能为空");
        }
        WarnRecord exist = warnRecordMapper.selectById(entity.getRecordId());
        if (exist == null) {
            throw new BusinessException(404, "预警记录不存在");
        }
        warnRecordMapper.updateById(entity);
        return warnRecordMapper.selectById(entity.getRecordId());
    }

    @Override
    public void delete(Long recordId) {
        WarnRecord exist = warnRecordMapper.selectById(recordId);
        if (exist == null) {
            throw new BusinessException(404, "预警记录不存在");
        }
        warnRecordMapper.deleteById(recordId);
    }

    @Override
    public List<WarnRecord> listByTaskId(Long taskId) {
        if (taskId == null) {
            throw new BusinessException("任务ID不能为空");
        }
        LambdaQueryWrapper<WarnRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarnRecord::getTaskId, taskId)
                .orderByDesc(WarnRecord::getPushTime);
        return warnRecordMapper.selectList(wrapper);
    }
}
