package com.enterprise.tasksuperviseserver.module.task.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskTemplateField;
import com.enterprise.tasksuperviseserver.module.task.service.TaskTemplateFieldService;
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
 * 任务模板字段接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/task/template-field")
@RequiredArgsConstructor
public class TaskTemplateFieldController {

    private final TaskTemplateFieldService taskTemplateFieldService;

    /**
     * 分页查询模板字段列表
     */
    @GetMapping("/page")
    public Result<Page<TaskTemplateField>> page(
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long templateId,
            @RequestParam(required = false) String fieldName) {
        return Result.success(taskTemplateFieldService.page(pageNo, pageSize, templateId, fieldName));
    }

    /**
     * 按模板ID查询字段列表
     */
    @GetMapping("/template/{templateId}")
    public Result<List<TaskTemplateField>> listByTemplateId(@PathVariable Long templateId) {
        return Result.success(taskTemplateFieldService.listByTemplateId(templateId));
    }

    /**
     * 获取字段详情
     */
    @GetMapping("/{fieldId}")
    public Result<TaskTemplateField> getDetail(@PathVariable Long fieldId) {
        return Result.success(taskTemplateFieldService.getDetail(fieldId));
    }

    /**
     * 新增字段
     */
    @PostMapping
    public Result<TaskTemplateField> create(@RequestBody TaskTemplateField field) {
        return Result.success(taskTemplateFieldService.create(field));
    }

    /**
     * 更新字段
     */
    @PutMapping
    public Result<TaskTemplateField> update(@RequestBody TaskTemplateField field) {
        return Result.success(taskTemplateFieldService.update(field));
    }

    /**
     * 删除字段
     */
    @DeleteMapping("/{fieldId}")
    public Result<Boolean> delete(@PathVariable Long fieldId) {
        return Result.success("删除成功", taskTemplateFieldService.delete(fieldId));
    }
}
