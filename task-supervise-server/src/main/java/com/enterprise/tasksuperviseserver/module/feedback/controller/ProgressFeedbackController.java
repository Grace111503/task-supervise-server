package com.enterprise.tasksuperviseserver.module.feedback.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.feedback.entity.ProgressFeedback;
import com.enterprise.tasksuperviseserver.module.feedback.service.ProgressFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

import java.util.List;
import java.util.Map;

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

    /**
     * 按 taskId 查询反馈列表（含关联文件）
     */
    @GetMapping("/task/{taskId}/with-files")
    public Result<List<ProgressFeedback>> listByTaskIdWithFiles(@PathVariable Long taskId) {
        return Result.success(progressFeedbackService.listByTaskIdWithFiles(taskId));
    }

    /**
     * 获取进度反馈详情（含关联文件）
     */
    @GetMapping("/{feedbackId}/with-files")
    public Result<ProgressFeedback> getDetailWithFiles(@PathVariable Long feedbackId) {
        return Result.success(progressFeedbackService.getDetailWithFiles(feedbackId));
    }

    /**
     * 添加反馈并关联文件
     * 请求体：{ "taskId": 1, "completedContent": "...", "nextPlan": "...", "progressPercent": 50, "fileIds": [1,2,3] }
     */
    @PostMapping("/add-with-files")
    public Result<ProgressFeedback> addWithFiles(@RequestBody Map<String, Object> body) {
        ProgressFeedback feedback = new ProgressFeedback();
        feedback.setTaskId(((Number) body.get("taskId")).longValue());
        if (body.containsKey("completedContent")) {
            feedback.setCompletedContent((String) body.get("completedContent"));
        }
        if (body.containsKey("nextPlan")) {
            feedback.setNextPlan((String) body.get("nextPlan"));
        }
        if (body.containsKey("progressPercent")) {
            feedback.setProgressPercent(((Number) body.get("progressPercent")).intValue());
        }

        @SuppressWarnings("unchecked")
        List<Long> fileIds = body.get("fileIds") != null
                ? ((List<Number>) body.get("fileIds")).stream().map(Number::longValue).toList()
                : List.of();

        return Result.success(progressFeedbackService.addWithFiles(feedback, fileIds));
    }

    /**
     * 获取任务的下一个阶段号
     */
    @GetMapping("/task/{taskId}/next-stage")
    public Result<Map<String, Integer>> getNextStage(@PathVariable Long taskId) {
        Integer nextStage = progressFeedbackService.getNextStage(taskId);
        return Result.success(Map.of("stage", nextStage));
    }

    /**
     * 一步提交反馈 + 上传文件（multipart/form-data）
     * <p>
     * 前端只需一次请求，同时上传文件和提交反馈内容。
     * <p>
     * 参数：
     * - taskId          (必填) 任务ID
     * - completedContent 完成内容
     * - nextPlan         下一步计划
     * - progressPercent  进度百分比
     * - files            文件数组（可选，支持多文件）
     */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ProgressFeedback> submit(
            @RequestParam("taskId") Long taskId,
            @RequestParam(value = "completedContent", required = false) String completedContent,
            @RequestParam(value = "nextPlan", required = false) String nextPlan,
            @RequestParam(value = "progressPercent", required = false) Integer progressPercent,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {

        ProgressFeedback feedback = new ProgressFeedback();
        feedback.setTaskId(taskId);
        feedback.setCompletedContent(completedContent);
        feedback.setNextPlan(nextPlan);
        feedback.setProgressPercent(progressPercent);

        return Result.success(progressFeedbackService.addWithFilesMultipart(feedback, files));
    }
}
