package com.enterprise.tasksuperviseserver.module.acceptance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.RectifyTask;
import com.enterprise.tasksuperviseserver.module.acceptance.mapper.RectifyTaskMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.service.RectifyTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 整改任务 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class RectifyTaskServiceImpl implements RectifyTaskService {

    private final RectifyTaskMapper rectifyTaskMapper;

    @Override
    public Page<RectifyTask> page(int pageNo, int pageSize) {
        Page<RectifyTask> page = Page.of(pageNo, pageSize);
        LambdaQueryWrapper<RectifyTask> wrapper = new LambdaQueryWrapper<RectifyTask>()
                .orderByDesc(RectifyTask::getCreateTime);
        return rectifyTaskMapper.selectPage(page, wrapper);
    }

    @Override
    public RectifyTask getDetail(Long rectifyId) {
        RectifyTask rectifyTask = rectifyTaskMapper.selectById(rectifyId);
        if (rectifyTask == null) {
            throw new BusinessException(404, "整改任务不存在");
        }
        return rectifyTask;
    }

    @Override
    public RectifyTask add(RectifyTask rectifyTask) {
        rectifyTask.setStatus(TaskConstant.RECTIFY_STATUS_PENDING);
        rectifyTask.setCreateTime(LocalDateTime.now());
        rectifyTaskMapper.insert(rectifyTask);
        return rectifyTask;
    }

    @Override
    public RectifyTask update(RectifyTask rectifyTask) {
        if (rectifyTask.getRectifyId() == null) {
            throw new BusinessException(400, "整改ID不能为空");
        }
        RectifyTask existing = rectifyTaskMapper.selectById(rectifyTask.getRectifyId());
        if (existing == null) {
            throw new BusinessException(404, "整改任务不存在");
        }
        rectifyTaskMapper.updateById(rectifyTask);
        return rectifyTaskMapper.selectById(rectifyTask.getRectifyId());
    }

    @Override
    public void delete(Long rectifyId) {
        RectifyTask existing = rectifyTaskMapper.selectById(rectifyId);
        if (existing == null) {
            throw new BusinessException(404, "整改任务不存在");
        }
        rectifyTaskMapper.deleteById(rectifyId);
    }

    @Override
    public List<RectifyTask> listByAcceptId(Long acceptId) {
        LambdaQueryWrapper<RectifyTask> wrapper = new LambdaQueryWrapper<RectifyTask>()
                .eq(RectifyTask::getAcceptId, acceptId)
                .orderByDesc(RectifyTask::getCreateTime);
        return rectifyTaskMapper.selectList(wrapper);
    }

    @Override
    public RectifyTask complete(Long rectifyId, String rectifyOpinion) {
        RectifyTask rectifyTask = rectifyTaskMapper.selectById(rectifyId);
        if (rectifyTask == null) {
            throw new BusinessException(404, "整改任务不存在");
        }
        rectifyTask.setCompleteTime(LocalDateTime.now());
        rectifyTask.setStatus(TaskConstant.RECTIFY_STATUS_RESUBMITTED);
        rectifyTask.setRectifyOpinion(rectifyOpinion);
        rectifyTaskMapper.updateById(rectifyTask);
        return rectifyTask;
    }
}
