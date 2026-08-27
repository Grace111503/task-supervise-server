package com.enterprise.tasksuperviseserver.module.acceptance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.OverdueAccountability;
import com.enterprise.tasksuperviseserver.module.acceptance.mapper.OverdueAccountabilityMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.service.OverdueAccountabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 逾期问责 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class OverdueAccountabilityServiceImpl implements OverdueAccountabilityService {

    private final OverdueAccountabilityMapper overdueAccountabilityMapper;

    @Override
    public Page<OverdueAccountability> page(int pageNo, int pageSize) {
        Page<OverdueAccountability> page = Page.of(pageNo, pageSize);
        LambdaQueryWrapper<OverdueAccountability> wrapper = new LambdaQueryWrapper<OverdueAccountability>()
                .orderByDesc(OverdueAccountability::getArchiveTime);
        return overdueAccountabilityMapper.selectPage(page, wrapper);
    }

    @Override
    public OverdueAccountability getDetail(Long accountabilityId) {
        OverdueAccountability accountability = overdueAccountabilityMapper.selectById(accountabilityId);
        if (accountability == null) {
            throw new BusinessException(404, "逾期问责记录不存在");
        }
        return accountability;
    }

    @Override
    public OverdueAccountability add(OverdueAccountability accountability) {
        accountability.setArchiveTime(LocalDateTime.now());
        overdueAccountabilityMapper.insert(accountability);
        return accountability;
    }

    @Override
    public OverdueAccountability update(OverdueAccountability accountability) {
        if (accountability.getAccountabilityId() == null) {
            throw new BusinessException(400, "问责ID不能为空");
        }
        OverdueAccountability existing = overdueAccountabilityMapper.selectById(accountability.getAccountabilityId());
        if (existing == null) {
            throw new BusinessException(404, "逾期问责记录不存在");
        }
        overdueAccountabilityMapper.updateById(accountability);
        return overdueAccountabilityMapper.selectById(accountability.getAccountabilityId());
    }

    @Override
    public void delete(Long accountabilityId) {
        OverdueAccountability existing = overdueAccountabilityMapper.selectById(accountabilityId);
        if (existing == null) {
            throw new BusinessException(404, "逾期问责记录不存在");
        }
        overdueAccountabilityMapper.deleteById(accountabilityId);
    }

    @Override
    public List<OverdueAccountability> listByTaskId(Long taskId) {
        LambdaQueryWrapper<OverdueAccountability> wrapper = new LambdaQueryWrapper<OverdueAccountability>()
                .eq(OverdueAccountability::getTaskId, taskId)
                .orderByDesc(OverdueAccountability::getArchiveTime);
        return overdueAccountabilityMapper.selectList(wrapper);
    }

    @Override
    public OverdueAccountability recordReason(Long taskId, String reason, Integer overdueDays) {
        OverdueAccountability a = new OverdueAccountability();
        a.setTaskId(taskId);
        a.setReason(reason);
        a.setOverdueDays(overdueDays);
        return add(a);
    }

    @Override
    public OverdueAccountability recordAccountability(Long taskId, String disposition, Integer overdueDays) {
        OverdueAccountability a = new OverdueAccountability();
        a.setTaskId(taskId);
        a.setDisposition(disposition);
        a.setOverdueDays(overdueDays);
        return add(a);
    }
}
