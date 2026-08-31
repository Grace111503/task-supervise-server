package com.enterprise.tasksuperviseserver.module.feedback.controller;

import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import com.enterprise.tasksuperviseserver.module.feedback.service.FileStorageService;
import com.enterprise.tasksuperviseserver.module.feedback.service.TaskFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件上传/下载/预览接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/file")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final TaskFileService taskFileService;

    /**
     * 上传成果材料
     *
     * @param file       MultipartFile
     * @param taskId     关联任务ID（可选）
     * @param feedbackId 关联反馈ID（可选）
     */
    @PostMapping("/upload")
    public Result<TaskFile> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "taskId", required = false) Long taskId,
            @RequestParam(value = "feedbackId", required = false) Long feedbackId) {
        return Result.success(fileStorageService.upload(file, taskId, feedbackId));
    }

    /**
     * 下载成果材料
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {
        FileStorageService.FileResource fr = fileStorageService.getFileResource(fileId);
        String encodedName = URLEncoder.encode(fr.taskFile().getOriginalName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fr.resource());
    }

    /**
     * 预览成果材料（inline 方式，浏览器直接展示）
     */
    @GetMapping("/preview/{fileId}")
    public ResponseEntity<Resource> preview(@PathVariable Long fileId) {
        FileStorageService.FileResource fr = fileStorageService.getFileResource(fileId);
        String encodedName = URLEncoder.encode(fr.taskFile().getOriginalName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fr.resource());
    }

    /**
     * 按任务ID查询文件列表
     */
    @GetMapping("/task/{taskId}")
    public Result<List<TaskFile>> listByTaskId(@PathVariable Long taskId) {
        return Result.success(taskFileService.listByTaskId(taskId));
    }

    /**
     * 按反馈ID查询文件列表
     */
    @GetMapping("/feedback/{feedbackId}")
    public Result<List<TaskFile>> listByFeedbackId(@PathVariable Long feedbackId) {
        return Result.success(taskFileService.listByFeedbackId(feedbackId));
    }

    /**
     * 绑定文件到反馈
     */
    @PutMapping("/{fileId}/bind/{feedbackId}")
    public Result<Void> bindToFeedback(@PathVariable Long fileId, @PathVariable Long feedbackId) {
        taskFileService.bindToFeedback(fileId, feedbackId);
        return Result.success();
    }

    /**
     * 软删除文件（管理员）
     */
    @DeleteMapping("/{fileId}")
    public Result<Void> softDelete(@PathVariable Long fileId) {
        taskFileService.softDelete(fileId);
        return Result.success("删除成功", null);
    }
}
