package com.enterprise.tasksuperviseserver.module.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskAssigneeMapper;
import com.enterprise.tasksuperviseserver.module.task.service.TaskAssigneeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务指派人 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class TaskAssigneeServiceImpl implements TaskAssigneeService {

    private final TaskAssigneeMapper taskAssigneeMapper;

    @Override
    public Page<TaskAssignee> page(long pageNo, long pageSize, Long taskId, Long userId) {
        LambdaQueryWrapper<TaskAssignee> wrapper = new LambdaQueryWrapper<>();
        if (taskId != null) {
            wrapper.eq(TaskAssignee::getTaskId, taskId);
        }
        if (userId != null) {
            wrapper.eq(TaskAssignee::getUserId, userId);
        }
        wrapper.orderByDesc(TaskAssignee::getCreatedAt);
        return taskAssigneeMapper.selectPage(Page.of(pageNo, pageSize), wrapper);
    }

    @Override
    public TaskAssignee getDetail(Long id) {
        TaskAssignee assignee = taskAssigneeMapper.selectById(id);
        if (assignee == null) {
            throw new BusinessException(404, "任务指派人不存在");
        }
        return assignee;
    }

    @Override
    public TaskAssignee create(TaskAssignee assignee) {
        assignee.setCreatedAt(LocalDateTime.now());
        taskAssigneeMapper.insert(assignee);
        return assignee;
    }

    @Override
    public TaskAssignee update(TaskAssignee assignee) {
        if (assignee.getAssigneeId() == null) {
            throw new BusinessException("任务指派人ID不能为空");
        }
        TaskAssignee existing = taskAssigneeMapper.selectById(assignee.getAssigneeId());
        if (existing == null) {
            throw new BusinessException(404, "任务指派人不存在");
        }
        taskAssigneeMapper.updateById(assignee);
        return taskAssigneeMapper.selectById(assignee.getAssigneeId());
    }

    @Override
    public boolean delete(Long id) {
        TaskAssignee existing = taskAssigneeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "任务指派人不存在");
        }
        return taskAssigneeMapper.deleteById(id) > 0;
    }

    @Override
    public List<TaskAssignee> listByTaskId(Long taskId) {
        return taskAssigneeMapper.selectList(new LambdaQueryWrapper<TaskAssignee>()
                .eq(TaskAssignee::getTaskId, taskId)
                .orderByAsc(TaskAssignee::getAssigneeId));
    }
}
