package com.enterprise.tasksuperviseserver.module.acceptance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.FlowConfig;
import com.enterprise.tasksuperviseserver.module.acceptance.service.FlowConfigService;
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
 * 流程配置接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/acceptance/flow-config")
@RequiredArgsConstructor
public class FlowConfigController {

    private final FlowConfigService flowConfigService;

    /**
     * 分页查询流程配置列表 (支持 flowName 模糊搜索)
     */
    @GetMapping("/page")
    public Result<Page<FlowConfig>> page(@RequestParam(defaultValue = "1") int pageNo,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(required = false) String flowName) {
        return Result.success(flowConfigService.page(pageNo, pageSize, flowName));
    }

    /**
     * 获取流程配置详情
     */
    @GetMapping("/{flowId}")
    public Result<FlowConfig> getDetail(@PathVariable Long flowId) {
        return Result.success(flowConfigService.getDetail(flowId));
    }

    /**
     * 新增流程配置
     */
    @PostMapping
    public Result<FlowConfig> add(@RequestBody FlowConfig flowConfig) {
        return Result.success(flowConfigService.add(flowConfig));
    }

    /**
     * 更新流程配置
     */
    @PutMapping
    public Result<FlowConfig> update(@RequestBody FlowConfig flowConfig) {
        return Result.success(flowConfigService.update(flowConfig));
    }

    /**
     * 删除流程配置
     */
    @DeleteMapping("/{flowId}")
    public Result<Void> delete(@PathVariable Long flowId) {
        flowConfigService.delete(flowId);
        return Result.success("删除成功", null);
    }

    /**
     * 流程配置列表（不分页，用于下拉选择，支持 flowName 模糊 + deptId 筛选）
     * GET /api/v1/acceptance/flow-config/list
     */
    @GetMapping("/list")
    public Result<List<FlowConfig>> list(@RequestParam(required = false) String flowName,
                                         @RequestParam(required = false) Long deptId) {
        return Result.success(flowConfigService.list(flowName, deptId));
    }
}
