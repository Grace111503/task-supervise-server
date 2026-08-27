package com.enterprise.tasksuperviseserver.module.dashboard.service;

import java.util.List;
import java.util.Map;

/**
 * 状态跟踪看板 Service
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
public interface DashboardService {

    /**
     * 首页六大状态任务卡片
     *
     * @return 六张卡片的 status / label / count 列表
     */
    List<Map<String, Object>> cards();

    /**
     * 按状态分页查询当前用户相关任务
     *
     * @param status   任务状态（1~6）
     * @param page     页码
     * @param pageSize 每页条数
     * @return { list, page, pageSize, total }
     */
    Map<String, Object> taskByStatus(Integer status, long page, long pageSize);
}
