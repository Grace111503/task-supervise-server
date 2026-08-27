package com.enterprise.tasksuperviseserver.module.acceptance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.OverdueAccountability;
import com.enterprise.tasksuperviseserver.module.acceptance.service.OverdueAccountabilityService;
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
 * 逾期问责接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/acceptance/accountability")
@RequiredArgsConstructor
public class OverdueAccountabilityController {

    private final OverdueAccountabilityService overdueAccountabilityService;

    /**
     * 分页查询逾期问责列表
     */
    @GetMapping("/page")
    public Result<Page<OverdueAccountability>> page(@RequestParam(defaultValue = "1") int pageNo,
                                                     @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(overdueAccountabilityService.page(pageNo, pageSize));
    }

    /**
     * 获取逾期问责详情
     */
    @GetMapping("/{accountabilityId}")
    public Result<OverdueAccountability> getDetail(@PathVariable Long accountabilityId) {
        return Result.success(overdueAccountabilityService.getDetail(accountabilityId));
    }

    /**
     * 新增逾期问责
     */
    @PostMapping
    public Result<OverdueAccountability> add(@RequestBody OverdueAccountability accountability) {
        return Result.success(overdueAccountabilityService.add(accountability));
    }

    /**
     * 更新逾期问责
     */
    @PutMapping
    public Result<OverdueAccountability> update(@RequestBody OverdueAccountability accountability) {
        return Result.success(overdueAccountabilityService.update(accountability));
    }

    /**
     * 删除逾期问责
     */
    @DeleteMapping("/{accountabilityId}")
    public Result<Void> delete(@PathVariable Long accountabilityId) {
        overdueAccountabilityService.delete(accountabilityId);
        return Result.success("删除成功", null);
    }

    /**
     * 按 taskId 查询逾期问责
     */
    @GetMapping("/task/{taskId}")
    public Result<List<OverdueAccountability>> listByTaskId(@PathVariable Long taskId) {
        return Result.success(overdueAccountabilityService.listByTaskId(taskId));
    }

    /**
     * 登记逾期原因
     * POST /api/v1/acceptance/accountability/overdue/{taskId}/reason
     * body: { reason, overdueDays? }
     */
    @PostMapping("/overdue/{taskId}/reason")
    public Result<OverdueAccountability> recordReason(@PathVariable Long taskId,
                                                      @RequestBody java.util.Map<String, Object> body) {
        String reason = body.get("reason") != null ? body.get("reason").toString() : null;
        Integer overdueDays = body.get("overdueDays") != null
                ? ((Number) body.get("overdueDays")).intValue() : null;
        return Result.success(overdueAccountabilityService.recordReason(taskId, reason, overdueDays));
    }

    /**
     * 登记追责
     * POST /api/v1/acceptance/accountability/overdue/{taskId}/accountability
     * body: { disposition, overdueDays? }
     */
    @PostMapping("/overdue/{taskId}/accountability")
    public Result<OverdueAccountability> recordAccountability(@PathVariable Long taskId,
                                                              @RequestBody java.util.Map<String, Object> body) {
        String disposition = body.get("disposition") != null ? body.get("disposition").toString() : null;
        Integer overdueDays = body.get("overdueDays") != null
                ? ((Number) body.get("overdueDays")).intValue() : null;
        return Result.success(overdueAccountabilityService.recordAccountability(taskId, disposition, overdueDays));
    }
}
