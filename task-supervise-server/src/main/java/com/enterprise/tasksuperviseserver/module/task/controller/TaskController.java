package com.enterprise.tasksuperviseserver.module.task.controller;

import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.service.TaskService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 任务接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 任务列表（分页）
     * 对齐前端 GET /task/list
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long assigneeId) {
        return Result.success(taskService.list(page, pageSize, status, priority, groupId, keyword, assigneeId));
    }

    /**
     * 任务统计
     * 对齐前端 GET /task/statistics
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        return Result.success(taskService.statistics());
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public Result<Task> getDetail(@PathVariable Long taskId) {
        return Result.success(taskService.getDetail(taskId));
    }

    /**
     * 新增任务
     */
    @PostMapping
    public Result<Task> create(@RequestBody Task task) {
        return Result.success(taskService.create(task));
    }

    /**
     * 更新任务
     */
    @PutMapping
    public Result<Task> update(@RequestBody Task task) {
        return Result.success(taskService.update(task));
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    public Result<Boolean> delete(@PathVariable Long taskId) {
        return Result.success("删除成功", taskService.delete(taskId));
    }

    /**
     * 指派任务
     * 对齐前端 POST /task/{id}/assign
     */
    @PostMapping("/{taskId}/assign")
    public Result<Void> assign(@PathVariable Long taskId, @RequestBody Map<String, Long> body) {
        taskService.assign(taskId, body.get("assigneeId"));
        return Result.success();
    }

    /**
     * 更新任务状态
     * 对齐前端 PUT /task/{id}/status
     */
    @PutMapping("/{taskId}/status")
    public Result<Void> updateStatus(@PathVariable Long taskId, @RequestBody Map<String, Integer> body) {
        taskService.updateStatus(taskId, body.get("status"));
        return Result.success();
    }

    /**
     * Excel 批量导入任务
     * Excel 格式：列A=标题, 列B=内容, 列C=优先级(1普通/2重要/3紧急)
     */
    @PostMapping("/batch-import")
    public Result<Map<String, Object>> batchImport(@RequestParam("file") MultipartFile file) throws IOException {
        return Result.success(taskService.batchImport(file.getBytes()));
    }

    /**
     * 批量分派任务
     * 请求体：{ "taskIds": [1,2,3], "assigneeId": 4 }
     */
    @PostMapping("/batch-assign")
    public Result<Map<String, Object>> batchAssign(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> taskIds = ((List<Number>) body.get("taskIds"))
                .stream().map(Number::longValue).toList();
        Long assigneeId = ((Number) body.get("assigneeId")).longValue();
        return Result.success(taskService.batchAssign(taskIds, assigneeId));
    }
}
