package com.enterprise.tasksuperviseserver.module.feedback.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.feedback.entity.ProgressFeedback;
import com.enterprise.tasksuperviseserver.module.feedback.service.ProgressFeedbackService;
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
 * 进度反馈接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/feedback/progress")
@RequiredArgsConstructor
public class ProgressFeedbackController {

    private final ProgressFeedbackService progressFeedbackService;

    /**
     * 分页查询进度反馈列表
     */
    @GetMapping("/page")
    public Result<Page<ProgressFeedback>> page(@RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(progressFeedbackService.page(pageNo, pageSize));
    }

    /**
     * 获取进度反馈详情
     */
    @GetMapping("/{feedbackId}")
    public Result<ProgressFeedback> getDetail(@PathVariable Long feedbackId) {
        return Result.success(progressFeedbackService.getDetail(feedbackId));
    }

    /**
     * 新增进度反馈
     */
    @PostMapping
    public Result<ProgressFeedback> add(@RequestBody ProgressFeedback feedback) {
        return Result.success(progressFeedbackService.add(feedback));
    }

    /**
     * 更新进度反馈
     */
    @PutMapping
    public Result<ProgressFeedback> update(@RequestBody ProgressFeedback feedback) {
        return Result.success(progressFeedbackService.update(feedback));
    }

    /**
     * 删除进度反馈
     */
    @DeleteMapping("/{feedbackId}")
    public Result<Void> delete(@PathVariable Long feedbackId) {
        progressFeedbackService.delete(feedbackId);
        return Result.success("删除成功", null);
    }

    /**
     * 按 taskId 查询反馈列表
     */
    @GetMapping("/task/{taskId}")
    public Result<List<ProgressFeedback>> listByTaskId(@PathVariable Long taskId) {
        return Result.success(progressFeedbackService.listByTaskId(taskId));
    }
}
