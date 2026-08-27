package com.enterprise.tasksuperviseserver.module.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskProgressNode;

import java.util.List;

/**
 * 任务进度节点 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface TaskProgressNodeService {

    /**
     * 分页查询进度节点列表
     */
    Page<TaskProgressNode> page(long pageNo, long pageSize, Long taskId, Integer status);

    /**
     * 获取进度节点详情
     */
    TaskProgressNode getDetail(Long nodeId);

    /**
     * 新增进度节点
     */
    TaskProgressNode create(TaskProgressNode node);

    /**
     * 更新进度节点
     */
    TaskProgressNode update(TaskProgressNode node);

    /**
     * 物理删除进度节点
     */
    boolean delete(Long nodeId);

    /**
     * 按 taskId 查询进度节点列表
     */
    List<TaskProgressNode> listByTaskId(Long taskId);
}
