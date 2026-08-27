package com.enterprise.tasksuperviseserver.module.acceptance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.FlowConfig;

import java.util.List;

/**
 * 流程配置 Service
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
public interface FlowConfigService {

    /**
     * 分页查询流程配置列表 (支持 flowName 模糊搜索)
     */
    Page<FlowConfig> page(int pageNo, int pageSize, String flowName);

    /**
     * 获取流程配置详情
     */
    FlowConfig getDetail(Long flowId);

    /**
     * 新增流程配置
     */
    FlowConfig add(FlowConfig flowConfig);

    /**
     * 更新流程配置
     */
    FlowConfig update(FlowConfig flowConfig);

    /**
     * 物理删除流程配置
     */
    void delete(Long flowId);

    /**
     * 查询流程配置列表（不分页，用于下拉选择，支持 flowName 模糊 + deptId 筛选）
     */
    List<FlowConfig> list(String flowName, Long deptId);
}
