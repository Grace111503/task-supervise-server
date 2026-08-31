package com.enterprise.tasksuperviseserver.module.acceptance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.Acceptance;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.RectifyTask;
import com.enterprise.tasksuperviseserver.module.acceptance.mapper.AcceptanceMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.mapper.RectifyTaskMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.service.AcceptanceService;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import com.enterprise.tasksuperviseserver.module.feedback.mapper.TaskFileMapper;
import com.enterprise.tasksuperviseserver.module.feedback.service.FileStorageService;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 验收 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcceptanceServiceImpl implements AcceptanceService {

    private final AcceptanceMapper acceptanceMapper;
    private final TaskMapper taskMapper;
    private final RectifyTaskMapper rectifyTaskMapper;
    private final TaskFileMapper taskFileMapper;
    private final FileStorageService fileStorageService;

    @Override
    public Page<Acceptance> page(int pageNo, int pageSize) {
        Page<Acceptance> page = Page.of(pageNo, pageSize);
        LambdaQueryWrapper<Acceptance> wrapper = new LambdaQueryWrapper<Acceptance>()
                .orderByDesc(Acceptance::getApplyTime);
        return acceptanceMapper.selectPage(page, wrapper);
    }

    @Override
    public Acceptance getDetail(Long acceptId) {
        Acceptance acceptance = acceptanceMapper.selectById(acceptId);
        if (acceptance == null) {
            throw new BusinessException(404, "验收记录不存在");
        }
        return acceptance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Acceptance add(Acceptance acceptance) {
        Long userId = UserContext.getUserId();
        acceptance.setApplicantId(userId);
        acceptance.setApplicantName(UserContext.getUsername());
        acceptance.setApplyTime(LocalDateTime.now());
        acceptance.setResult(TaskConstant.ACCEPT_RESULT_PENDING);
        acceptanceMapper.insert(acceptance);

        // 同步更新 Task 状态为待验收
        if (acceptance.getTaskId() != null) {
            Task task = taskMapper.selectById(acceptance.getTaskId());
            if (task != null) {
                task.setStatus(TaskConstant.STATUS_STR_PENDING_ACCEPT);
                task.setAcceptResult(TaskConstant.ACCEPT_RESULT_PENDING);
                task.setUpdatedAt(LocalDateTime.now());
                taskMapper.updateById(task);
            }
        }

        return acceptance;
    }

    @Override
    public Acceptance update(Acceptance acceptance) {
        if (acceptance.getAcceptId() == null) {
            throw new BusinessException(400, "验收ID不能为空");
        }
        Acceptance existing = acceptanceMapper.selectById(acceptance.getAcceptId());
        if (existing == null) {
            throw new BusinessException(404, "验收记录不存在");
        }
        acceptanceMapper.updateById(acceptance);
        return acceptanceMapper.selectById(acceptance.getAcceptId());
    }

    @Override
    public void delete(Long acceptId) {
        Acceptance existing = acceptanceMapper.selectById(acceptId);
        if (existing == null) {
            throw new BusinessException(404, "验收记录不存在");
        }
        acceptanceMapper.deleteById(acceptId);
    }

    @Override
    public List<Acceptance> listByTaskId(Long taskId) {
        LambdaQueryWrapper<Acceptance> wrapper = new LambdaQueryWrapper<Acceptance>()
                .eq(Acceptance::getTaskId, taskId)
                .orderByDesc(Acceptance::getApplyTime);
        return acceptanceMapper.selectList(wrapper);
    }

    @Override
    public Acceptance approve(Long acceptId, Integer result, String opinion) {
        Acceptance acceptance = acceptanceMapper.selectById(acceptId);
        if (acceptance == null) {
            throw new BusinessException(404, "验收记录不存在");
        }
        if (result == null) {
            throw new BusinessException(400, "验收结果不能为空");
        }
        acceptance.setAcceptorId(UserContext.getUserId());
        acceptance.setAcceptTime(LocalDateTime.now());
        acceptance.setResult(result);
        acceptance.setOpinion(opinion);
        acceptanceMapper.updateById(acceptance);
        return acceptance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Acceptance verify(Long acceptId, Integer result, String opinion) {
        Acceptance acceptance = approve(acceptId, result, opinion);

        if (acceptance.getTaskId() == null) {
            return acceptance;
        }

        Task task = taskMapper.selectById(acceptance.getTaskId());
        if (task == null) {
            return acceptance;
        }

        Long userId = UserContext.getUserId();

        if (Integer.valueOf(TaskConstant.ACCEPT_RESULT_PASS).equals(result)) {
            // 核验通过 → 任务完成
            task.setStatus(TaskConstant.STATUS_STR_COMPLETED);
            task.setAcceptResult(TaskConstant.ACCEPT_RESULT_PASS);
            task.setAcceptRemark(opinion);
            task.setAcceptedAt(LocalDateTime.now());
            task.setAcceptedBy(userId);
            task.setUpdatedAt(LocalDateTime.now());
            taskMapper.updateById(task);
            log.info("验收通过: taskId={}, acceptId={}", task.getId(), acceptId);
        } else if (Integer.valueOf(TaskConstant.ACCEPT_RESULT_REJECT).equals(result)) {
            // 退回整改 → 任务回到进行中 + 自动创建整改任务
            task.setStatus(TaskConstant.STATUS_STR_IN_PROGRESS);
            task.setAcceptResult(TaskConstant.ACCEPT_RESULT_REJECT);
            task.setRejectRemark(opinion);
            task.setRejectedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            taskMapper.updateById(task);

            // 自动创建整改任务
            RectifyTask rectifyTask = new RectifyTask();
            rectifyTask.setTaskId(task.getId());
            rectifyTask.setAcceptId(acceptId);
            rectifyTask.setRectifyReason(opinion);
            rectifyTask.setStatus(TaskConstant.RECTIFY_STATUS_PENDING);
            rectifyTask.setCreateTime(LocalDateTime.now());
            rectifyTaskMapper.insert(rectifyTask);

            log.info("验收退回，已创建整改任务: taskId={}, rectifyId={}", task.getId(), rectifyTask.getRectifyId());
        }

        return acceptance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Acceptance applyWithFiles(Acceptance acceptance, MultipartFile[] files) {
        // 1. 创建验收申请
        Acceptance created = add(acceptance);

        // 2. 上传文件并关联
        if (files != null && files.length > 0) {
            List<Long> fileIds = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    TaskFile taskFile = fileStorageService.upload(file, acceptance.getTaskId(), null);
                    fileIds.add(taskFile.getFileId());
                }
            }
            // 关联文件到验收记录（通过 feedbackId 字段复用，或单独处理）
            // 这里暂时不关联，因为 TaskFile 的 feedbackId 是针对 progress_feedback 的
            // 前端可通过 taskId 查询所有文件
        }

        return created;
    }

    @Override
    public List<Acceptance> listByTaskIdWithFiles(Long taskId) {
        List<Acceptance> list = listByTaskId(taskId);
        for (Acceptance acceptance : list) {
            List<TaskFile> files = taskFileMapper.selectList(
                    new LambdaQueryWrapper<TaskFile>()
                            .eq(TaskFile::getTaskId, taskId)
                            .orderByAsc(TaskFile::getUploadTime));
            acceptance.setFiles(files);
        }
        return list;
    }
}
