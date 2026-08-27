package com.enterprise.tasksuperviseserver.module.dashboard.controller;

import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 状态跟踪看板接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 首页六大状态任务卡片
     * 返回：[{ status, label, count }, ...]
     */
    @GetMapping("/cards")
    public Result<List<Map<String, Object>>> cards() {
        return Result.success(dashboardService.cards());
    }

    /**
     * 按状态分页查询任务列表
     *
     * @param status 任务状态（1=待接收 2=进行中 3=待反馈 4=待验收 5=已完成 6=已逾期）
     */
    @GetMapping("/task/{status}")
    public Result<Map<String, Object>> taskByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(dashboardService.taskByStatus(status, page, pageSize));
    }
}
