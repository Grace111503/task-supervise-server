package com.enterprise.tasksuperviseserver.module.acceptance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.Acceptance;

import java.util.List;

/**
 * 验收 Service
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
public interface AcceptanceService {

    /**
     * 分页查询验收列表
     */
    Page<Acceptance> page(int pageNo, int pageSize);

    /**
     * 获取验收详情
     */
    Acceptance getDetail(Long acceptId);

    /**
     * 新增验收申请 (applicantId 取当前登录用户, applyTime 取当前时间, result = ACCEPT_RESULT_PENDING)
     */
    Acceptance add(Acceptance acceptance);

    /**
     * 更新验收
     */
    Acceptance update(Acceptance acceptance);

    /**
     * 物理删除验收
     */
    void delete(Long acceptId);

    /**
     * 按 taskId 查询验收记录
     */
    List<Acceptance> listByTaskId(Long taskId);

    /**
     * 验收审批 (设置 acceptorId 取当前登录用户, acceptTime 取当前时间, result, opinion)
     */
    Acceptance approve(Long acceptId, Integer result, String opinion);

    /**
     * 验收核验（核验结果 + 核验意见，核验通过时任务状态置为已完成）
     */
    Acceptance verify(Long acceptId, Integer result, String opinion);
}
