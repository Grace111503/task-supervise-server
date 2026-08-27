package com.enterprise.tasksuperviseserver.module.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import com.enterprise.tasksuperviseserver.module.feedback.mapper.TaskFileMapper;
import com.enterprise.tasksuperviseserver.module.feedback.service.TaskFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务文件 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class TaskFileServiceImpl implements TaskFileService {

    private final TaskFileMapper taskFileMapper;

    @Override
    public Page<TaskFile> page(int pageNo, int pageSize) {
        Page<TaskFile> page = Page.of(pageNo, pageSize);
        LambdaQueryWrapper<TaskFile> wrapper = new LambdaQueryWrapper<TaskFile>()
                .orderByDesc(TaskFile::getUploadTime);
        return taskFileMapper.selectPage(page, wrapper);
    }

    @Override
    public TaskFile getDetail(Long fileId) {
        TaskFile taskFile = taskFileMapper.selectById(fileId);
        if (taskFile == null) {
            throw new BusinessException(404, "任务文件不存在");
        }
        return taskFile;
    }

    @Override
    public TaskFile add(TaskFile taskFile) {
        taskFile.setUploadTime(LocalDateTime.now());
        taskFileMapper.insert(taskFile);
        return taskFile;
    }

    @Override
    public TaskFile update(TaskFile taskFile) {
        if (taskFile.getFileId() == null) {
            throw new BusinessException(400, "文件ID不能为空");
        }
        TaskFile existing = taskFileMapper.selectById(taskFile.getFileId());
        if (existing == null) {
            throw new BusinessException(404, "任务文件不存在");
        }
        taskFileMapper.updateById(taskFile);
        return taskFileMapper.selectById(taskFile.getFileId());
    }

    @Override
    public void delete(Long fileId) {
        TaskFile existing = taskFileMapper.selectById(fileId);
        if (existing == null) {
            throw new BusinessException(404, "任务文件不存在");
        }
        taskFileMapper.deleteById(fileId);
    }

    @Override
    public List<TaskFile> listByTaskId(Long taskId) {
        LambdaQueryWrapper<TaskFile> wrapper = new LambdaQueryWrapper<TaskFile>()
                .eq(TaskFile::getTaskId, taskId)
                .orderByDesc(TaskFile::getUploadTime);
        return taskFileMapper.selectList(wrapper);
    }

    @Override
    public List<TaskFile> listByFeedbackId(Long feedbackId) {
        LambdaQueryWrapper<TaskFile> wrapper = new LambdaQueryWrapper<TaskFile>()
                .eq(TaskFile::getFeedbackId, feedbackId)
                .orderByDesc(TaskFile::getUploadTime);
        return taskFileMapper.selectList(wrapper);
    }
}
