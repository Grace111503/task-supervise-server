package com.enterprise.tasksuperviseserver.module.warn.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRule;
import com.enterprise.tasksuperviseserver.module.warn.service.WarnRuleService;
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

/**
 * 预警规则接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/warn/rule")
@RequiredArgsConstructor
public class WarnRuleController {

    private final WarnRuleService warnRuleService;

    /**
     * 分页查询预警规则列表
     * GET /api/v1/warn/rule/page
     */
    @GetMapping("/page")
    public Result<Page<WarnRule>> page(@RequestParam(defaultValue = "1") int pageNo,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      @RequestParam(required = false) String ruleName,
                                      @RequestParam(required = false) Integer level) {
        return Result.success(warnRuleService.page(pageNo, pageSize, ruleName, level));
    }

    /**
     * 获取预警规则详情
     * GET /api/v1/warn/rule/{ruleId}
     */
    @GetMapping("/{ruleId}")
    public Result<WarnRule> getById(@PathVariable Long ruleId) {
        return Result.success(warnRuleService.getById(ruleId));
    }

    /**
     * 新增预警规则
     * POST /api/v1/warn/rule
     */
    @PostMapping
    public Result<WarnRule> create(@RequestBody WarnRule entity) {
        return Result.success(warnRuleService.create(entity));
    }

    /**
     * 更新预警规则
     * PUT /api/v1/warn/rule/{ruleId}
     */
    @PutMapping("/{ruleId}")
    public Result<WarnRule> update(@PathVariable Long ruleId, @RequestBody WarnRule entity) {
        entity.setRuleId(ruleId);
        return Result.success(warnRuleService.update(entity));
    }

    /**
     * 物理删除预警规则
     * DELETE /api/v1/warn/rule/{ruleId}
     */
    @DeleteMapping("/{ruleId}")
    public Result<Void> delete(@PathVariable Long ruleId) {
        warnRuleService.delete(ruleId);
        return Result.success();
    }
}
