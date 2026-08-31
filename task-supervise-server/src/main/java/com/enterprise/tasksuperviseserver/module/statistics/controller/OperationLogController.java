package com.enterprise.tasksuperviseserver.module.statistics.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.statistics.entity.OperationLog;
import com.enterprise.tasksuperviseserver.module.statistics.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作日志接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/statistics/log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志列表（支持按模块/任务/人员/部门/时间区间筛选）
     * GET /api/v1/statistics/log/page
     */
    @GetMapping("/page")
    public Result<Page<OperationLog>> page(@RequestParam(defaultValue = "1") int pageNo,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          @RequestParam(required = false) String module,
                                          @RequestParam(required = false) Long taskId,
                                          @RequestParam(required = false) Long operatorId,
                                          @RequestParam(required = false) Long deptId,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return Result.success(operationLogService.page(pageNo, pageSize, module, taskId, operatorId, deptId, startTime, endTime));
    }

    /**
     * 获取操作日志详情
     * GET /api/v1/statistics/log/{logId}
     */
    @GetMapping("/{logId}")
    public Result<OperationLog> getById(@PathVariable Long logId) {
        return Result.success(operationLogService.getById(logId));
    }

    /**
     * 新增操作日志
     * POST /api/v1/statistics/log
     */
    @PostMapping
    public Result<OperationLog> create(@RequestBody OperationLog entity) {
        return Result.success(operationLogService.create(entity));
    }

    /**
     * 更新操作日志
     * PUT /api/v1/statistics/log/{logId}
     */
    @PutMapping("/{logId}")
    public Result<OperationLog> update(@PathVariable Long logId, @RequestBody OperationLog entity) {
        entity.setLogId(logId);
        return Result.success(operationLogService.update(entity));
    }

    /**
     * 物理删除操作日志
     * DELETE /api/v1/statistics/log/{logId}
     */
    @DeleteMapping("/{logId}")
    public Result<Void> delete(@PathVariable Long logId) {
        operationLogService.delete(logId);
        return Result.success();
    }

    /**
     * 验证日志哈希完整性（防篡改校验）
     * GET /api/v1/statistics/log/{logId}/verify
     */
    @GetMapping("/{logId}/verify")
    public Result<Map<String, Object>> verify(@PathVariable Long logId) {
        return Result.success(operationLogService.verify(logId));
    }
}
