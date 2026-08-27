package com.enterprise.tasksuperviseserver.module.feedback.service.impl;

import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import com.enterprise.tasksuperviseserver.module.feedback.mapper.TaskFileMapper;
import com.enterprise.tasksuperviseserver.module.feedback.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件存储 Service 实现（本地文件系统）
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final TaskFileMapper taskFileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.allowed-types}")
    private String allowedTypes;

    @Value("${file.max-size}")
    private long maxSize;

    private Set<String> allowedExtensions;

    @PostConstruct
    public void init() {
        allowedExtensions = Arrays.stream(allowedTypes.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            log.warn("创建上传目录失败: {}", e.getMessage());
        }
    }

    @Override
    public TaskFile upload(MultipartFile file, Long taskId, Long feedbackId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalName)) {
            throw new BusinessException("文件名不能为空");
        }

        String extension = getExtension(originalName);
        if (!allowedExtensions.isEmpty() && !allowedExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型: " + extension);
        }

        if (file.getSize() > maxSize * 1024 * 1024) {
            throw new BusinessException("文件大小超过限制（" + maxSize + "MB）");
        }

        // 按日期分目录: uploads/2026/08/27/uuid.docx
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path targetDir = Paths.get(uploadDir, datePath);
        Path targetPath = targetDir.resolve(storedName);

        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new BusinessException("文件保存失败: " + e.getMessage());
        }

        // 构造相对路径存储到数据库
        String relativePath = datePath + "/" + storedName;

        TaskFile taskFile = new TaskFile();
        taskFile.setTaskId(taskId);
        taskFile.setFeedbackId(feedbackId);
        taskFile.setFileName(originalName);
        taskFile.setFilePath(relativePath);
        taskFile.setFileType(extension.toUpperCase());
        taskFile.setFileSize(file.getSize());
        taskFile.setUploadTime(LocalDateTime.now());
        taskFileMapper.insert(taskFile);

        log.info("文件上传成功: {} → {}", originalName, relativePath);
        return taskFile;
    }

    @Override
    public FileResource getFileResource(Long fileId) {
        TaskFile taskFile = taskFileMapper.selectById(fileId);
        if (taskFile == null) {
            throw new BusinessException(404, "文件不存在");
        }

        Path filePath = Paths.get(uploadDir, taskFile.getFilePath());
        if (!Files.exists(filePath)) {
            throw new BusinessException(404, "文件已被删除或丢失");
        }

        Resource resource = new FileSystemResource(filePath);
        return new FileResource(resource, taskFile);
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
    }
}
