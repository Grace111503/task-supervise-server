package com.enterprise.tasksuperviseserver.module.feedback.service.impl;

import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.config.MinioConfig;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import com.enterprise.tasksuperviseserver.module.feedback.mapper.TaskFileMapper;
import com.enterprise.tasksuperviseserver.module.feedback.service.FileStorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
 * 文件存储 Service 实现
 * <p>
 * 支持两种存储模式（通过 file.storage-mode 配置切换）：
 * <ul>
 *   <li>local  — 本地文件系统（默认，开发环境）</li>
 *   <li>minio  — MinIO 对象存储（生产环境）</li>
 * </ul>
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
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.allowed-types}")
    private String allowedTypes;

    @Value("${file.max-size}")
    private long maxSize;

    @Value("${file.storage-mode:local}")
    private String storageMode;

    private Set<String> allowedExtensions;
    private boolean useMinio;

    @PostConstruct
    public void init() {
        allowedExtensions = Arrays.stream(allowedTypes.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        useMinio = "minio".equalsIgnoreCase(storageMode);

        if (!useMinio) {
            try {
                Files.createDirectories(Paths.get(uploadDir));
            } catch (Exception e) {
                log.warn("创建上传目录失败: {}", e.getMessage());
            }
        } else {
            log.info("文件存储模式: MinIO (bucket={}, endpoint={})",
                    minioConfig.getBucket(), "configured");
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

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String objectKey = datePath + "/" + storedName;

        if (useMinio) {
            uploadToMinio(objectKey, file);
        } else {
            uploadToLocal(objectKey, file);
        }

        TaskFile taskFile = new TaskFile();
        taskFile.setTaskId(taskId);
        taskFile.setFeedbackId(feedbackId);
        taskFile.setOriginalName(originalName);
        taskFile.setStoredName(storedName);
        taskFile.setFilePath(objectKey);
        taskFile.setFileType(extension.toUpperCase());
        taskFile.setFileSize(file.getSize());
        taskFile.setUploaderId(UserContext.getUserId());
        taskFile.setUploaderName(UserContext.getName() != null ? UserContext.getName() : UserContext.getUsername());
        taskFile.setUploadTime(LocalDateTime.now());
        taskFileMapper.insert(taskFile);

        log.info("文件上传成功: {} → {} ({})", originalName, objectKey, useMinio ? "minio" : "local");
        return taskFile;
    }

    @Override
    public FileResource getFileResource(Long fileId) {
        TaskFile taskFile = taskFileMapper.selectById(fileId);
        if (taskFile == null) {
            throw new BusinessException(404, "文件不存在");
        }

        if (useMinio) {
            byte[] bytes = downloadFromMinio(taskFile.getFilePath());
            Resource resource = new ByteArrayResource(bytes);
            return new FileResource(resource, taskFile);
        } else {
            Path filePath = Paths.get(uploadDir, taskFile.getFilePath());
            if (!Files.exists(filePath)) {
                throw new BusinessException(404, "文件已被删除或丢失");
            }
            Resource resource = new FileSystemResource(filePath);
            return new FileResource(resource, taskFile);
        }
    }

    /**
     * 上传到本地文件系统
     */
    private void uploadToLocal(String objectKey, MultipartFile file) {
        Path targetPath = Paths.get(uploadDir, objectKey);
        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath.toFile());
        } catch (Exception e) {
            throw new BusinessException("文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 上传到 MinIO
     */
    private void uploadToMinio(String objectKey, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectKey)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .build());
        } catch (Exception e) {
            throw new BusinessException("MinIO 上传失败: " + e.getMessage());
        }
    }

    /**
     * 从 MinIO 下载文件
     */
    private byte[] downloadFromMinio(String objectKey) {
        try (InputStream is = minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(objectKey)
                .build());
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException(404, "文件下载失败: " + e.getMessage());
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
    }
}
