package com.enterprise.tasksuperviseserver.module.acceptance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.OverdueAccountability;

import java.util.List;

/**
 * 逾期问责 Service
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
public interface OverdueAccountabilityService {

    /**
     * 分页查询逾期问责列表
     */
    Page<OverdueAccountability> page(int pageNo, int pageSize);

    /**
     * 获取逾期问责详情
     */
    OverdueAccountability getDetail(Long accountabilityId);

    /**
     * 新增逾期问责 (archiveTime 取当前时间)
     */
    OverdueAccountability add(OverdueAccountability accountability);

    /**
     * 更新逾期问责
     */
    OverdueAccountability update(OverdueAccountability accountability);

    /**
     * 物理删除逾期问责
     */
    void delete(Long accountabilityId);

    /**
     * 按 taskId 查询逾期问责
     */
    List<OverdueAccountability> listByTaskId(Long taskId);

    /**
     * 登记逾期原因（按 taskId，原因写 reason）
     */
    OverdueAccountability recordReason(Long taskId, String reason, Integer overdueDays);

    /**
     * 登记追责（按 taskId，处理结果写 disposition）
     */
    OverdueAccountability recordAccountability(Long taskId, String disposition, Integer overdueDays);
}
