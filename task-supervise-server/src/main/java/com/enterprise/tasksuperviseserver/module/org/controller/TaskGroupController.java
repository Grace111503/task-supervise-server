package com.enterprise.tasksuperviseserver.module.org.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.org.entity.TaskGroup;
import com.enterprise.tasksuperviseserver.module.org.service.TaskGroupService;
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
 * 任务分组接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/org/group")
@RequiredArgsConstructor
public class TaskGroupController {

    private final TaskGroupService taskGroupService;

    /**
     * 分页查询分组列表
     * GET /api/v1/org/group/page
     */
    @GetMapping("/page")
    public Result<Page<TaskGroup>> page(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) String keyword) {
        return Result.success(taskGroupService.page(page, pageSize, keyword));
    }

    /**
     * 查询分组详情
     * GET /api/v1/org/group/{groupId}
     */
    @GetMapping("/{groupId}")
    public Result<TaskGroup> detail(@PathVariable Long groupId) {
        return Result.success(taskGroupService.detail(groupId));
    }

    /**
     * 新增分组
     * POST /api/v1/org/group
     */
    @PostMapping
    public Result<TaskGroup> create(@RequestBody TaskGroup taskGroup) {
        return Result.success("新增成功", taskGroupService.create(taskGroup));
    }

    /**
     * 更新分组
     * PUT /api/v1/org/group
     */
    @PutMapping
    public Result<TaskGroup> update(@RequestBody TaskGroup taskGroup) {
        return Result.success("更新成功", taskGroupService.update(taskGroup));
    }

    /**
     * 删除分组
     * DELETE /api/v1/org/group/{groupId}
     */
    @DeleteMapping("/{groupId}")
    public Result<Void> delete(@PathVariable Long groupId) {
        taskGroupService.delete(groupId);
        return Result.success("删除成功", null);
    }
}
