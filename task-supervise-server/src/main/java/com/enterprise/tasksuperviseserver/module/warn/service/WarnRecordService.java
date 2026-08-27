package com.enterprise.tasksuperviseserver.module.warn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRecord;

import java.util.List;

/**
 * 预警记录 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface WarnRecordService {

    /**
     * 分页查询预警记录列表
     */
    Page<WarnRecord> page(int pageNo, int pageSize, Long taskId, Integer level);

    /**
     * 获取预警记录详情
     */
    WarnRecord getById(Long recordId);

    /**
     * 新增预警记录（pushTime 自动设置为当前时间）
     */
    WarnRecord create(WarnRecord entity);

    /**
     * 更新预警记录
     */
    WarnRecord update(WarnRecord entity);

    /**
     * 物理删除预警记录
     */
    void delete(Long recordId);

    /**
     * 按 taskId 查询预警记录列表
     */
    List<WarnRecord> listByTaskId(Long taskId);
}
