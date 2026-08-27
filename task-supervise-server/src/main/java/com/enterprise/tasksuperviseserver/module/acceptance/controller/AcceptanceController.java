package com.enterprise.tasksuperviseserver.module.acceptance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.Acceptance;
import com.enterprise.tasksuperviseserver.module.acceptance.service.AcceptanceService;
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
 * 验收接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/acceptance/acceptance")
@RequiredArgsConstructor
public class AcceptanceController {

    private final AcceptanceService acceptanceService;

    /**
     * 分页查询验收列表
     */
    @GetMapping("/page")
    public Result<Page<Acceptance>> page(@RequestParam(defaultValue = "1") int pageNo,
                                         @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(acceptanceService.page(pageNo, pageSize));
    }

    /**
     * 获取验收详情
     */
    @GetMapping("/{acceptId}")
    public Result<Acceptance> getDetail(@PathVariable Long acceptId) {
        return Result.success(acceptanceService.getDetail(acceptId));
    }

    /**
     * 新增验收申请
     */
    @PostMapping
    public Result<Acceptance> add(@RequestBody Acceptance acceptance) {
        return Result.success(acceptanceService.add(acceptance));
    }

    /**
     * 更新验收
     */
    @PutMapping
    public Result<Acceptance> update(@RequestBody Acceptance acceptance) {
        return Result.success(acceptanceService.update(acceptance));
    }

    /**
     * 删除验收
     */
    @DeleteMapping("/{acceptId}")
    public Result<Void> delete(@PathVariable Long acceptId) {
        acceptanceService.delete(acceptId);
        return Result.success("删除成功", null);
    }

    /**
     * 按 taskId 查询验收记录
     */
    @GetMapping("/task/{taskId}")
    public Result<List<Acceptance>> listByTaskId(@PathVariable Long taskId) {
        return Result.success(acceptanceService.listByTaskId(taskId));
    }

    /**
     * 验收审批
     */
    @PutMapping("/{acceptId}/approve")
    public Result<Acceptance> approve(@PathVariable Long acceptId,
                                      @RequestParam Integer result,
                                      @RequestParam(required = false) String opinion) {
        return Result.success(acceptanceService.approve(acceptId, result, opinion));
    }

    /**
     * 验收核验
     * POST /api/v1/acceptance/acceptance/{acceptId}/verify
     * body: { result, opinion? }
     * 核验通过时任务状态自动标记为已完成
     */
    @PostMapping("/{acceptId}/verify")
    public Result<Acceptance> verify(@PathVariable Long acceptId,
                                     @RequestBody java.util.Map<String, Object> body) {
        Integer result = ((Number) body.get("result")).intValue();
        String opinion = body.get("opinion") != null ? body.get("opinion").toString() : null;
        return Result.success(acceptanceService.verify(acceptId, result, opinion));
    }
}
