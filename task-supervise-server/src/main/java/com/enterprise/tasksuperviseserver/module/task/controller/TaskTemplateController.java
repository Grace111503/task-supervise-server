package com.enterprise.tasksuperviseserver.module.task.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskTemplate;
import com.enterprise.tasksuperviseserver.module.task.service.TaskTemplateService;
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
 * 任务模板接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/task/template")
@RequiredArgsConstructor
public class TaskTemplateController {

    private final TaskTemplateService taskTemplateService;

    /**
     * 分页查询模板列表
     */
    @GetMapping("/page")
    public Result<Page<TaskTemplate>> page(
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) Integer templateType) {
        return Result.success(taskTemplateService.page(pageNo, pageSize, templateName, templateType));
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/{templateId}")
    public Result<TaskTemplate> getDetail(@PathVariable Long templateId) {
        return Result.success(taskTemplateService.getDetail(templateId));
    }

    /**
     * 新增模板
     */
    @PostMapping
    public Result<TaskTemplate> create(@RequestBody TaskTemplate template) {
        return Result.success(taskTemplateService.create(template));
    }

    /**
     * 更新模板
     */
    @PutMapping
    public Result<TaskTemplate> update(@RequestBody TaskTemplate template) {
        return Result.success(taskTemplateService.update(template));
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/{templateId}")
    public Result<Boolean> delete(@PathVariable Long templateId) {
        return Result.success("删除成功", taskTemplateService.delete(templateId));
    }
}
