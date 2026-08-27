package com.enterprise.tasksuperviseserver.module.task.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;
import com.enterprise.tasksuperviseserver.module.task.service.TaskAssigneeService;
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
 * 任务指派人接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/task/assignee")
@RequiredArgsConstructor
public class TaskAssigneeController {

    private final TaskAssigneeService taskAssigneeService;

    /**
     * 分页查询指派人列表
     */
    @GetMapping("/page")
    public Result<Page<TaskAssignee>> page(
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long userId) {
        return Result.success(taskAssigneeService.page(pageNo, pageSize, taskId, userId));
    }

    /**
     * 获取指派人详情
     */
    @GetMapping("/{id}")
    public Result<TaskAssignee> getDetail(@PathVariable Long id) {
        return Result.success(taskAssigneeService.getDetail(id));
    }

    /**
     * 新增指派人
     */
    @PostMapping
    public Result<TaskAssignee> create(@RequestBody TaskAssignee assignee) {
        return Result.success(taskAssigneeService.create(assignee));
    }

    /**
     * 更新指派人
     */
    @PutMapping
    public Result<TaskAssignee> update(@RequestBody TaskAssignee assignee) {
        return Result.success(taskAssigneeService.update(assignee));
    }

    /**
     * 删除指派人
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success("删除成功", taskAssigneeService.delete(id));
    }

    /**
     * 按 taskId 查询指派人列表
     */
    @GetMapping("/task/{taskId}")
    public Result<List<TaskAssignee>> listByTaskId(@PathVariable Long taskId) {
        return Result.success(taskAssigneeService.listByTaskId(taskId));
    }
}
