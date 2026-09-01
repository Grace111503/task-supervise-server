package com.enterprise.tasksuperviseserver.module.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.module.feedback.entity.ProgressFeedback;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import com.enterprise.tasksuperviseserver.module.feedback.mapper.ProgressFeedbackMapper;
import com.enterprise.tasksuperviseserver.module.feedback.mapper.TaskFileMapper;
import com.enterprise.tasksuperviseserver.module.feedback.service.FileStorageService;
import com.enterprise.tasksuperviseserver.module.feedback.service.ProgressFeedbackService;
import com.enterprise.tasksuperviseserver.module.feedback.websocket.FeedbackWebSocket;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskAssigneeMapper;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 进度反馈 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressFeedbackServiceImpl implements ProgressFeedbackService {

    private final ProgressFeedbackMapper progressFeedbackMapper;
    private final TaskFileMapper taskFileMapper;
    private final TaskMapper taskMapper;
    private final TaskAssigneeMapper taskAssigneeMapper;
    private final FileStorageService fileStorageService;

    @Override
    public Page<ProgressFeedback> page(int pageNo, int pageSize) {
        Page<ProgressFeedback> page = Page.of(pageNo, pageSize);
        LambdaQueryWrapper<ProgressFeedback> wrapper = new LambdaQueryWrapper<ProgressFeedback>()
                .orderByDesc(ProgressFeedback::getFeedbackTime);
        return progressFeedbackMapper.selectPage(page, wrapper);
    }

    @Override
    public ProgressFeedback getDetail(Long feedbackId) {
        ProgressFeedback feedback = progressFeedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException(404, "进度反馈不存在");
        }
        return feedback;
    }

    @Override
    public ProgressFeedback add(ProgressFeedback feedback) {
        Long userId = UserContext.getUserId();
        // 优先使用真实姓名，回退到登录用户名
        String username = UserContext.getName() != null ? UserContext.getName() : UserContext.getUsername();

        // 权限检查：只有执行人（非创建人）可以提交反馈
        Task taskForCheck = taskMapper.selectById(feedback.getTaskId());
        if (taskForCheck != null) {
            String role = UserContext.getRole();
            if (!"admin".equals(role) && !"manager".equals(role)) {
                if (taskForCheck.getCreatorId() != null && taskForCheck.getCreatorId().equals(userId)) {
                    throw new BusinessException(403, "任务发布者不能提交反馈");
                }
            }
        }

        feedback.setUserId(userId);
        feedback.setUserName(username);
        feedback.setFeedbackTime(LocalDateTime.now());

        // 自动计算阶段号
        if (feedback.getStage() == null) {
            feedback.setStage(getNextStage(feedback.getTaskId()));
        }

        progressFeedbackMapper.insert(feedback);

        // 进度达到100%时自动进入待验收
        try {
            Task task = taskMapper.selectById(feedback.getTaskId());
            if (task != null && feedback.getProgressPercent() != null
                    && feedback.getProgressPercent() >= 100
                    && !"completed".equals(task.getStatus())
                    && !"pending_accept".equals(task.getStatus())) {

                boolean shouldTransition = false;

                // 多人模式：需要所有执行人都达到100%才跳待验收
                if (task.getAssigneeMode() != null
                        && task.getAssigneeMode() == TaskConstant.ASSIGNEE_MODE_MULTI) {
                    shouldTransition = allAssigneesCompleted(task.getId());
                } else {
                    // 单人模式：直接跳待验收
                    shouldTransition = true;
                }

                if (shouldTransition) {
                    task.setStatus("pending_accept");
                    task.setAcceptResult(0);
                    task.setUpdatedAt(LocalDateTime.now());
                    taskMapper.updateById(task);
                    log.info("任务进入待验收: taskId={}, progress={}", task.getId(), feedback.getProgressPercent());
                }
            }
        } catch (Exception e) {
            log.warn("自动进入待验收失败: {}", e.getMessage());
        }

        // 发送实时通知给任务创建人
        try {
            Task task = taskMapper.selectById(feedback.getTaskId());
            if (task != null && task.getCreatorId() != null && !task.getCreatorId().equals(userId)) {
                String notification = String.format(
                        "{\"taskId\":%d,\"feedbackId\":%d,\"userName\":\"%s\",\"stage\":%d}",
                        feedback.getTaskId(), feedback.getFeedbackId(),
                        username != null ? username : "未知用户", feedback.getStage());
                FeedbackWebSocket.sendToUser(task.getCreatorId(),
                        FeedbackWebSocket.TYPE_FEEDBACK_ADDED, notification);
            }
        } catch (Exception e) {
            log.warn("发送反馈通知失败: {}", e.getMessage());
        }

        return feedback;
    }

    @Override
    public ProgressFeedback update(ProgressFeedback feedback) {
        if (feedback.getFeedbackId() == null) {
            throw new BusinessException(400, "反馈ID不能为空");
        }
        ProgressFeedback existing = progressFeedbackMapper.selectById(feedback.getFeedbackId());
        if (existing == null) {
            throw new BusinessException(404, "进度反馈不存在");
        }
        progressFeedbackMapper.updateById(feedback);
        return progressFeedbackMapper.selectById(feedback.getFeedbackId());
    }

    @Override
    public void delete(Long feedbackId) {
        ProgressFeedback existing = progressFeedbackMapper.selectById(feedbackId);
        if (existing == null) {
            throw new BusinessException(404, "进度反馈不存在");
        }
        progressFeedbackMapper.deleteById(feedbackId);
    }

    @Override
    public List<ProgressFeedback> listByTaskId(Long taskId) {
        LambdaQueryWrapper<ProgressFeedback> wrapper = new LambdaQueryWrapper<ProgressFeedback>()
                .eq(ProgressFeedback::getTaskId, taskId)
                .orderByAsc(ProgressFeedback::getStage);
        return progressFeedbackMapper.selectList(wrapper);
    }

    @Override
    public List<ProgressFeedback> listByTaskIdWithFiles(Long taskId) {
        List<ProgressFeedback> feedbacks = listByTaskId(taskId);
        if (feedbacks.isEmpty()) {
            return feedbacks;
        }
        // 批量加载关联文件
        for (ProgressFeedback feedback : feedbacks) {
            List<TaskFile> files = taskFileMapper.selectList(
                    new LambdaQueryWrapper<TaskFile>()
                            .eq(TaskFile::getFeedbackId, feedback.getFeedbackId())
                            .orderByAsc(TaskFile::getUploadTime));
            feedback.setFiles(files);
        }
        return feedbacks;
    }

    @Override
    public ProgressFeedback getDetailWithFiles(Long feedbackId) {
        ProgressFeedback feedback = getDetail(feedbackId);
        List<TaskFile> files = taskFileMapper.selectList(
                new LambdaQueryWrapper<TaskFile>()
                        .eq(TaskFile::getFeedbackId, feedback.getFeedbackId())
                        .orderByAsc(TaskFile::getUploadTime));
        feedback.setFiles(files);
        return feedback;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProgressFeedback addWithFiles(ProgressFeedback feedback, List<Long> fileIds) {
        // 添加反馈
        ProgressFeedback created = add(feedback);

        // 关联文件
        if (fileIds != null && !fileIds.isEmpty()) {
            for (Long fileId : fileIds) {
                TaskFile taskFile = taskFileMapper.selectById(fileId);
                if (taskFile != null) {
                    taskFile.setFeedbackId(created.getFeedbackId());
                    taskFileMapper.updateById(taskFile);
                }
            }
        }

        return getDetailWithFiles(created.getFeedbackId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProgressFeedback addWithFilesMultipart(ProgressFeedback feedback, MultipartFile[] files) {
        // 1. 先上传所有文件，拿到 fileId 列表
        List<Long> fileIds = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    TaskFile taskFile = fileStorageService.upload(file, feedback.getTaskId(), null);
                    fileIds.add(taskFile.getFileId());
                }
            }
        }

        // 2. 调用已有的 addWithFiles 方法创建反馈并关联文件
        return addWithFiles(feedback, fileIds);
    }

    @Override
    public Integer getNextStage(Long taskId) {
        Long currentUserId = UserContext.getUserId();
        // 按用户独立计算阶段号（多人模式下每人独立递增）
        LambdaQueryWrapper<ProgressFeedback> wrapper = new LambdaQueryWrapper<ProgressFeedback>()
                .eq(ProgressFeedback::getTaskId, taskId)
                .eq(ProgressFeedback::getUserId, currentUserId)
                .orderByDesc(ProgressFeedback::getStage)
                .last("LIMIT 1");
        ProgressFeedback latest = progressFeedbackMapper.selectOne(wrapper);
        if (latest == null || latest.getStage() == null) {
            return 1;
        }
        return latest.getStage() + 1;
    }

    @Override
    public List<Map<String, Object>> listAssigneeProgress(Long taskId) {
        // 查询任务的所有执行人
        LambdaQueryWrapper<TaskAssignee> aw = new LambdaQueryWrapper<TaskAssignee>()
                .eq(TaskAssignee::getTaskId, taskId)
                .orderByAsc(TaskAssignee::getAssigneeType);
        List<TaskAssignee> assignees = taskAssigneeMapper.selectList(aw);

        List<Map<String, Object>> result = new ArrayList<>();
        for (TaskAssignee assignee : assignees) {
            Map<String, Object> item = new HashMap<>();
            item.put("userId", assignee.getUserId());
            item.put("userName", assignee.getAssigneeName());
            item.put("assigneeType", assignee.getAssigneeType());

            // 查询该执行人的最新反馈
            LambdaQueryWrapper<ProgressFeedback> fw = new LambdaQueryWrapper<ProgressFeedback>()
                    .eq(ProgressFeedback::getTaskId, taskId)
                    .eq(ProgressFeedback::getUserId, assignee.getUserId())
                    .orderByDesc(ProgressFeedback::getStage)
                    .last("LIMIT 1");
            ProgressFeedback latest = progressFeedbackMapper.selectOne(fw);

            if (latest != null) {
                item.put("latestProgress", latest.getProgressPercent());
                item.put("stage", latest.getStage());
                item.put("latestContent", latest.getCompletedContent());
                item.put("latestTime", latest.getFeedbackTime());
            } else {
                item.put("latestProgress", 0);
                item.put("stage", 0);
                item.put("latestContent", null);
                item.put("latestTime", null);
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 检查多人任务的所有执行人是否都已完成（进度100%）
     */
    private boolean allAssigneesCompleted(Long taskId) {
        LambdaQueryWrapper<TaskAssignee> aw = new LambdaQueryWrapper<TaskAssignee>()
                .eq(TaskAssignee::getTaskId, taskId);
        List<TaskAssignee> assignees = taskAssigneeMapper.selectList(aw);
        if (assignees.isEmpty()) return false;

        for (TaskAssignee assignee : assignees) {
            LambdaQueryWrapper<ProgressFeedback> fw = new LambdaQueryWrapper<ProgressFeedback>()
                    .eq(ProgressFeedback::getTaskId, taskId)
                    .eq(ProgressFeedback::getUserId, assignee.getUserId())
                    .orderByDesc(ProgressFeedback::getStage)
                    .last("LIMIT 1");
            ProgressFeedback latest = progressFeedbackMapper.selectOne(fw);
            if (latest == null || latest.getProgressPercent() == null || latest.getProgressPercent() < 100) {
                return false;
            }
        }
        return true;
    }
}
