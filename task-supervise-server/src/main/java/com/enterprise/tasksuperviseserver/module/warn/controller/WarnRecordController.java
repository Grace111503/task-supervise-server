package com.enterprise.tasksuperviseserver.module.warn.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRecord;
import com.enterprise.tasksuperviseserver.module.warn.service.WarnRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 预警记录接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/warn/record")
@RequiredArgsConstructor
public class WarnRecordController {

    private final WarnRecordService warnRecordService;

    /**
     * 分页查询预警记录列表
     * GET /api/v1/warn/record/page
     */
    @GetMapping("/page")
    public Result<Page<WarnRecord>> page(@RequestParam(defaultValue = "1") int pageNo,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        @RequestParam(required = false) Long taskId,
                                        @RequestParam(required = false) Integer level) {
        return Result.success(warnRecordService.page(pageNo, pageSize, taskId, level));
    }

    /**
     * 获取预警记录详情
     * GET /api/v1/warn/record/{recordId}
     */
    @GetMapping("/{recordId}")
    public Result<WarnRecord> getById(@PathVariable Long recordId) {
        return Result.success(warnRecordService.getById(recordId));
    }

    /**
     * 新增预警记录
     * POST /api/v1/warn/record
     */
    @PostMapping
    public Result<WarnRecord> create(@RequestBody WarnRecord entity) {
        return Result.success(warnRecordService.create(entity));
    }

    /**
     * 更新预警记录
     * PUT /api/v1/warn/record/{recordId}
     */
    @PutMapping("/{recordId}")
    public Result<WarnRecord> update(@PathVariable Long recordId, @RequestBody WarnRecord entity) {
        entity.setRecordId(recordId);
        return Result.success(warnRecordService.update(entity));
    }

    /**
     * 物理删除预警记录
     * DELETE /api/v1/warn/record/{recordId}
     */
    @DeleteMapping("/{recordId}")
    public Result<Void> delete(@PathVariable Long recordId) {
        warnRecordService.delete(recordId);
        return Result.success();
    }

    /**
     * 按任务ID查询预警记录列表
     * GET /api/v1/warn/record/task/{taskId}
     */
    @GetMapping("/task/{taskId}")
    public Result<List<WarnRecord>> listByTaskId(@PathVariable Long taskId) {
        return Result.success(warnRecordService.listByTaskId(taskId));
    }
}
