package com.enterprise.tasksuperviseserver.module.statistics.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.statistics.entity.StatisticsReport;

/**
 * 统计报表 Service
 */
public interface StatisticsReportService {

    /**
     * 分页查询统计报表列表（支持 period/periodValue 筛选 + deptId/userId 筛选）
     */
    Page<StatisticsReport> page(int pageNo, int pageSize, String period, String periodValue, Long deptId, Long userId);

    /**
     * 获取统计报表详情
     */
    StatisticsReport getById(Long reportId);

    /**
     * 新增统计报表（generateTime 自动设置为当前时间）
     */
    StatisticsReport create(StatisticsReport entity);

    /**
     * 更新统计报表
     */
    StatisticsReport update(StatisticsReport entity);

    /**
     * 物理删除统计报表
     */
    void delete(Long reportId);
}
