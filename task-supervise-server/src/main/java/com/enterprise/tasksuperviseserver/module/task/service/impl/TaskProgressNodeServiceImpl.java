package com.enterprise.tasksuperviseserver.module.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskProgressNode;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskProgressNodeMapper;
import com.enterprise.tasksuperviseserver.module.task.service.TaskProgressNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务进度节点 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class TaskProgressNodeServiceImpl implements TaskProgressNodeService {

    private final TaskProgressNodeMapper taskProgressNodeMapper;

    @Override
    public Page<TaskProgressNode> page(long pageNo, long pageSize, Long taskId, Integer status) {
        LambdaQueryWrapper<TaskProgressNode> wrapper = new LambdaQueryWrapper<>();
        if (taskId != null) {
            wrapper.eq(TaskProgressNode::getTaskId, taskId);
        }
        if (status != null) {
            wrapper.eq(TaskProgressNode::getStatus, status);
        }
        wrapper.orderByAsc(TaskProgressNode::getStage);
        return taskProgressNodeMapper.selectPage(Page.of(pageNo, pageSize), wrapper);
    }

    @Override
    public TaskProgressNode getDetail(Long nodeId) {
        TaskProgressNode node = taskProgressNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new BusinessException(404, "任务进度节点不存在");
        }
        return node;
    }

    @Override
    public TaskProgressNode create(TaskProgressNode node) {
        taskProgressNodeMapper.insert(node);
        return node;
    }

    @Override
    public TaskProgressNode update(TaskProgressNode node) {
        if (node.getNodeId() == null) {
            throw new BusinessException("任务进度节点ID不能为空");
        }
        TaskProgressNode existing = taskProgressNodeMapper.selectById(node.getNodeId());
        if (existing == null) {
            throw new BusinessException(404, "任务进度节点不存在");
        }
        taskProgressNodeMapper.updateById(node);
        return taskProgressNodeMapper.selectById(node.getNodeId());
    }

    @Override
    public boolean delete(Long nodeId) {
        TaskProgressNode existing = taskProgressNodeMapper.selectById(nodeId);
        if (existing == null) {
            throw new BusinessException(404, "任务进度节点不存在");
        }
        return taskProgressNodeMapper.deleteById(nodeId) > 0;
    }

    @Override
    public List<TaskProgressNode> listByTaskId(Long taskId) {
        return taskProgressNodeMapper.selectList(new LambdaQueryWrapper<TaskProgressNode>()
                .eq(TaskProgressNode::getTaskId, taskId)
                .orderByAsc(TaskProgressNode::getStage));
    }
}
