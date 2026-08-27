package com.enterprise.tasksuperviseserver.module.warn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRule;

/**
 * 预警规则 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface WarnRuleService {

    /**
     * 分页查询预警规则列表（支持 ruleName 模糊搜索 + level 筛选）
     */
    Page<WarnRule> page(int pageNo, int pageSize, String ruleName, Integer level);

    /**
     * 获取预警规则详情
     */
    WarnRule getById(Long ruleId);

    /**
     * 新增预警规则（createTime 自动设置为当前时间）
     */
    WarnRule create(WarnRule entity);

    /**
     * 更新预警规则
     */
    WarnRule update(WarnRule entity);

    /**
     * 物理删除预警规则
     */
    void delete(Long ruleId);
}
