package com.enterprise.tasksuperviseserver.module.feedback.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import com.enterprise.tasksuperviseserver.module.feedback.service.TaskFileService;
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
 * 任务文件接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/feedback/file")
@RequiredArgsConstructor
public class TaskFileController {

    private final TaskFileService taskFileService;

    /**
     * 分页查询任务文件列表
     */
    @GetMapping("/page")
    public Result<Page<TaskFile>> page(@RequestParam(defaultValue = "1") int pageNo,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(taskFileService.page(pageNo, pageSize));
    }

    /**
     * 获取任务文件详情
     */
    @GetMapping("/{fileId}")
    public Result<TaskFile> getDetail(@PathVariable Long fileId) {
        return Result.success(taskFileService.getDetail(fileId));
    }

    /**
     * 新增任务文件
     */
    @PostMapping
    public Result<TaskFile> add(@RequestBody TaskFile taskFile) {
        return Result.success(taskFileService.add(taskFile));
    }

    /**
     * 更新任务文件
     */
    @PutMapping
    public Result<TaskFile> update(@RequestBody TaskFile taskFile) {
        return Result.success(taskFileService.update(taskFile));
    }

    /**
     * 删除任务文件
     */
    @DeleteMapping("/{fileId}")
    public Result<Void> delete(@PathVariable Long fileId) {
        taskFileService.delete(fileId);
        return Result.success("删除成功", null);
    }

    /**
     * 按 taskId 查询文件列表
     */
    @GetMapping("/task/{taskId}")
    public Result<List<TaskFile>> listByTaskId(@PathVariable Long taskId) {
        return Result.success(taskFileService.listByTaskId(taskId));
    }

    /**
     * 按 feedbackId 查询文件
     */
    @GetMapping("/feedback/{feedbackId}")
    public Result<List<TaskFile>> listByFeedbackId(@PathVariable Long feedbackId) {
        return Result.success(taskFileService.listByFeedbackId(feedbackId));
    }
}
