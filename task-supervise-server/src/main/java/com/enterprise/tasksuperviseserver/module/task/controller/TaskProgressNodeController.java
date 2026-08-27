package com.enterprise.tasksuperviseserver.module.task.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskProgressNode;
import com.enterprise.tasksuperviseserver.module.task.service.TaskProgressNodeService;
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
 * 任务进度节点接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/task/progress-node")
@RequiredArgsConstructor
public class TaskProgressNodeController {

    private final TaskProgressNodeService taskProgressNodeService;

    /**
     * 分页查询进度节点列表
     */
    @GetMapping("/page")
    public Result<Page<TaskProgressNode>> page(
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Integer status) {
        return Result.success(taskProgressNodeService.page(pageNo, pageSize, taskId, status));
    }

    /**
     * 获取进度节点详情
     */
    @GetMapping("/{nodeId}")
    public Result<TaskProgressNode> getDetail(@PathVariable Long nodeId) {
        return Result.success(taskProgressNodeService.getDetail(nodeId));
    }

    /**
     * 新增进度节点
     */
    @PostMapping
    public Result<TaskProgressNode> create(@RequestBody TaskProgressNode node) {
        return Result.success(taskProgressNodeService.create(node));
    }

    /**
     * 更新进度节点
     */
    @PutMapping
    public Result<TaskProgressNode> update(@RequestBody TaskProgressNode node) {
        return Result.success(taskProgressNodeService.update(node));
    }

    /**
     * 删除进度节点
     */
    @DeleteMapping("/{nodeId}")
    public Result<Boolean> delete(@PathVariable Long nodeId) {
        return Result.success("删除成功", taskProgressNodeService.delete(nodeId));
    }

    /**
     * 按 taskId 查询进度节点列表
     */
    @GetMapping("/task/{taskId}")
    public Result<List<TaskProgressNode>> listByTaskId(@PathVariable Long taskId) {
        return Result.success(taskProgressNodeService.listByTaskId(taskId));
    }
}
