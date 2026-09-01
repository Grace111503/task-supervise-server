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
    @PutMapping("/{taskId}")
    public Result<Task> update(@PathVariable Long taskId, @RequestBody Task task) {
        task.setId(taskId);
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
     * 更新任务状态（支持字符串和整数两种格式）
     * 对齐前端 PUT /task/{id}/status
     */
    @PutMapping("/{taskId}/status")
    public Result<Void> updateStatus(@PathVariable Long taskId, @RequestBody Map<String, Object> body) {
        Object statusObj = body.get("status");
        if (statusObj instanceof String statusStr) {
            // 字符串状态直接使用
            taskService.updateStatus(taskId, statusStr);
        } else if (statusObj instanceof Number statusNum) {
            // 整数状态码需要转换
            taskService.updateStatus(taskId, statusNum.intValue());
        }
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
        return Result.success(taskService.batchAssignResult(taskIds, assigneeId));
    }

    /**
     * 多人协办模式分派任务
     * 请求体：{ "taskId": 1, "assigneeIds": [2,3,4], "primaryId": 2 }
     */
    @PostMapping("/assign-multi")
    public Result<Void> assignMulti(@RequestBody Map<String, Object> body) {
        Long taskId = ((Number) body.get("taskId")).longValue();
        @SuppressWarnings("unchecked")
        List<Long> assigneeIds = ((List<Number>) body.get("assigneeIds"))
                .stream().map(Number::longValue).toList();
        Long primaryId = body.get("primaryId") != null ? ((Number) body.get("primaryId")).longValue() : null;
        taskService.assignMulti(taskId, assigneeIds, primaryId);
        return Result.success();
    }

    /**
     * 根据模板创建任务
     * 请求体：{ "templateId": 1, "title": "自定义标题", ... }
     */
    @PostMapping("/create-by-template")
    public Result<Task> createByTemplate(@RequestBody Map<String, Object> body) {
        Long templateId = ((Number) body.get("templateId")).longValue();
        Task task = new Task();
        if (body.containsKey("title")) {
            task.setTitle((String) body.get("title"));
        }
        if (body.containsKey("description")) {
            task.setDescription((String) body.get("description"));
        }
        if (body.containsKey("deadline")) {
            task.setDeadline(java.time.LocalDateTime.parse((String) body.get("deadline")));
        }
        if (body.containsKey("deptId")) {
            task.setDeptId(((Number) body.get("deptId")).longValue());
        }
        if (body.containsKey("groupId")) {
            task.setGroupId(((Number) body.get("groupId")).longValue());
        }
        return Result.success(taskService.createByTemplate(templateId, task));
    }

    /**
     * 按分组查询任务列表
     */
    @GetMapping("/group/{groupId}")
    public Result<Map<String, Object>> listByGroup(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(taskService.listByGroup(groupId, page, pageSize));
    }

    /**
     * 驳回已完成的任务（管理员/主管）
     * 请求体：{ "rejectRemark": "原因" }
     */
    @PutMapping("/{taskId}/reject")
    public Result<Void> reject(@PathVariable Long taskId, @RequestBody Map<String, String> body) {
        taskService.reject(taskId, body.get("rejectRemark"));
        return Result.success();
    }

    /**
     * 验收任务（管理员/主管）
     * 请求体：{ "acceptResult": 1, "acceptRemark": "通过" }
     */
    @PutMapping("/{taskId}/accept")
    public Result<Void> accept(@PathVariable Long taskId, @RequestBody Map<String, Object> body) {
        Integer acceptResult = (Integer) body.get("acceptResult");
        String acceptRemark = (String) body.get("acceptRemark");
        taskService.accept(taskId, acceptResult, acceptRemark);
        return Result.success();
    }

    /**
     * 获取任务全流程时间线
     */
    @GetMapping("/{taskId}/timeline")
    public Result<List<Map<String, Object>>> getTimeline(@PathVariable Long taskId) {
        return Result.success(taskService.getTimeline(taskId));
    }

    /**
     * 逾期任务清单（分页）
     */
    @GetMapping("/overdue/list")
    public Result<Map<String, Object>> overdueList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long assigneeId) {
        return Result.success(taskService.overdueList(page, pageSize, groupId, assigneeId));
    }

    /**
     * 批量处置逾期任务
     * 请求体：{ "taskIds": [1,2], "action": "complete|extend|reassign", "param": {...} }
     */
    @PutMapping("/batch-overdue-action")
    public Result<Map<String, Object>> batchOverdueAction(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> taskIds = ((List<Number>) body.get("taskIds"))
                .stream().map(Number::longValue).toList();
        String action = (String) body.get("action");
        @SuppressWarnings("unchecked")
        Map<String, Object> param = (Map<String, Object>) body.get("param");
        return Result.success(taskService.batchOverdueAction(taskIds, action, param));
    }
}
