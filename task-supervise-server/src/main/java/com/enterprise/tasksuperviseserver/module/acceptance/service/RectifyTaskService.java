package com.enterprise.tasksuperviseserver.module.acceptance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.RectifyTask;

import java.util.List;

/**
 * 整改任务 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface RectifyTaskService {

    /**
     * 分页查询整改任务列表
     */
    Page<RectifyTask> page(int pageNo, int pageSize);

    /**
     * 获取整改任务详情
     */
    RectifyTask getDetail(Long rectifyId);

    /**
     * 新增整改任务 (status = RECTIFY_STATUS_PENDING, createTime 取当前时间)
     */
    RectifyTask add(RectifyTask rectifyTask);

    /**
     * 更新整改任务
     */
    RectifyTask update(RectifyTask rectifyTask);

    /**
     * 物理删除整改任务
     */
    void delete(Long rectifyId);

    /**
     * 按 acceptId 查询整改任务
     */
    List<RectifyTask> listByAcceptId(Long acceptId);

    /**
     * 完成整改 (设置 completeTime 取当前时间, status = RECTIFY_STATUS_RESUBMITTED)
     */
    RectifyTask complete(Long rectifyId, String rectifyOpinion);
}
