package com.enterprise.tasksuperviseserver.module.warn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRule;
import com.enterprise.tasksuperviseserver.module.warn.mapper.WarnRuleMapper;
import com.enterprise.tasksuperviseserver.module.warn.service.WarnRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 预警规则 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class WarnRuleServiceImpl implements WarnRuleService {

    private final WarnRuleMapper warnRuleMapper;

    @Override
    public Page<WarnRule> page(int pageNo, int pageSize, String ruleName, Integer level) {
        LambdaQueryWrapper<WarnRule> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(ruleName)) {
            wrapper.like(WarnRule::getRuleName, ruleName);
        }
        if (level != null) {
            wrapper.eq(WarnRule::getLevel, level);
        }
        wrapper.orderByDesc(WarnRule::getCreateTime);
        return warnRuleMapper.selectPage(Page.of(pageNo, pageSize), wrapper);
    }

    @Override
    public WarnRule getById(Long ruleId) {
        WarnRule rule = warnRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new BusinessException(404, "预警规则不存在");
        }
        return rule;
    }

    @Override
    public WarnRule create(WarnRule entity) {
        entity.setRuleId(null);
        entity.setCreateTime(LocalDateTime.now());
        warnRuleMapper.insert(entity);
        return entity;
    }

    @Override
    public WarnRule update(WarnRule entity) {
        if (entity.getRuleId() == null) {
            throw new BusinessException("规则ID不能为空");
        }
        WarnRule exist = warnRuleMapper.selectById(entity.getRuleId());
        if (exist == null) {
            throw new BusinessException(404, "预警规则不存在");
        }
        warnRuleMapper.updateById(entity);
        return warnRuleMapper.selectById(entity.getRuleId());
    }

    @Override
    public void delete(Long ruleId) {
        WarnRule exist = warnRuleMapper.selectById(ruleId);
        if (exist == null) {
            throw new BusinessException(404, "预警规则不存在");
        }
        warnRuleMapper.deleteById(ruleId);
    }
}
