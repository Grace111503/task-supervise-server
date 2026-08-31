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
import java.util.Map;

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

    /**
     * 批量添加指派人
     * 请求体：{ "taskId": 1, "userIds": [2,3,4], "assigneeType": 1 }
     */
    @PostMapping("/batch")
    public Result<List<TaskAssignee>> batchCreate(@RequestBody Map<String, Object> body) {
        Long taskId = ((Number) body.get("taskId")).longValue();
        @SuppressWarnings("unchecked")
        List<Long> userIds = ((List<Number>) body.get("userIds"))
                .stream().map(Number::longValue).toList();
        Integer assigneeType = body.get("assigneeType") != null ? ((Number) body.get("assigneeType")).intValue() : 1;
        return Result.success(taskAssigneeService.batchCreate(taskId, userIds, assigneeType));
    }

    /**
     * 更新指派类型
     * 请求体：{ "assigneeType": 2 }
     */
    @PutMapping("/{id}/type")
    public Result<Void> updateAssigneeType(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer assigneeType = body.get("assigneeType");
        taskAssigneeService.updateAssigneeType(id, assigneeType);
        return Result.success();
    }

    /**
     * 按任务ID和指派类型查询指派人列表
     */
    @GetMapping("/task/{taskId}/type/{assigneeType}")
    public Result<List<TaskAssignee>> listByTaskIdAndType(
            @PathVariable Long taskId,
            @PathVariable Integer assigneeType) {
        return Result.success(taskAssigneeService.listByTaskIdAndType(taskId, assigneeType));
    }
}
