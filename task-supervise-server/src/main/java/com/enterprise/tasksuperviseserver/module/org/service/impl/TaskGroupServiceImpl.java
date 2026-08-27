package com.enterprise.tasksuperviseserver.module.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.org.entity.TaskGroup;
import com.enterprise.tasksuperviseserver.module.org.mapper.TaskGroupMapper;
import com.enterprise.tasksuperviseserver.module.org.service.TaskGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 任务分组 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class TaskGroupServiceImpl implements TaskGroupService {

    private final TaskGroupMapper taskGroupMapper;

    @Override
    public Page<TaskGroup> page(int page, int pageSize, String keyword) {
        Page<TaskGroup> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<TaskGroup> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(TaskGroup::getGroupName, keyword);
        }
        wrapper.orderByDesc(TaskGroup::getCreateTime);
        return taskGroupMapper.selectPage(p, wrapper);
    }

    @Override
    public TaskGroup detail(Long groupId) {
        TaskGroup taskGroup = taskGroupMapper.selectById(groupId);
        if (taskGroup == null) {
            throw new BusinessException(404, "分组不存在");
        }
        return taskGroup;
    }

    @Override
    public TaskGroup create(TaskGroup taskGroup) {
        taskGroup.setCreateTime(LocalDateTime.now());
        taskGroupMapper.insert(taskGroup);
        return taskGroup;
    }

    @Override
    public TaskGroup update(TaskGroup taskGroup) {
        TaskGroup existing = taskGroupMapper.selectById(taskGroup.getGroupId());
        if (existing == null) {
            throw new BusinessException(404, "分组不存在");
        }
        taskGroupMapper.updateById(taskGroup);
        return taskGroupMapper.selectById(taskGroup.getGroupId());
    }

    @Override
    public boolean delete(Long groupId) {
        return taskGroupMapper.deleteById(groupId) > 0;
    }
}
