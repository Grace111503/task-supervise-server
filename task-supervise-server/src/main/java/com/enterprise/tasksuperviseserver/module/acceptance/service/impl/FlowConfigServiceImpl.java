package com.enterprise.tasksuperviseserver.module.acceptance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.FlowConfig;
import com.enterprise.tasksuperviseserver.module.acceptance.mapper.FlowConfigMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.service.FlowConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 流程配置 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class FlowConfigServiceImpl implements FlowConfigService {

    private final FlowConfigMapper flowConfigMapper;

    @Override
    public Page<FlowConfig> page(int pageNo, int pageSize, String flowName) {
        Page<FlowConfig> page = Page.of(pageNo, pageSize);
        LambdaQueryWrapper<FlowConfig> wrapper = new LambdaQueryWrapper<FlowConfig>()
                .like(StringUtils.hasText(flowName), FlowConfig::getFlowName, flowName)
                .orderByDesc(FlowConfig::getCreateTime);
        return flowConfigMapper.selectPage(page, wrapper);
    }

    @Override
    public FlowConfig getDetail(Long flowId) {
        FlowConfig flowConfig = flowConfigMapper.selectById(flowId);
        if (flowConfig == null) {
            throw new BusinessException(404, "流程配置不存在");
        }
        return flowConfig;
    }

    @Override
    public FlowConfig add(FlowConfig flowConfig) {
        flowConfigMapper.insert(flowConfig);
        return flowConfig;
    }

    @Override
    public FlowConfig update(FlowConfig flowConfig) {
        if (flowConfig.getFlowId() == null) {
            throw new BusinessException(400, "流程ID不能为空");
        }
        FlowConfig existing = flowConfigMapper.selectById(flowConfig.getFlowId());
        if (existing == null) {
            throw new BusinessException(404, "流程配置不存在");
        }
        flowConfigMapper.updateById(flowConfig);
        return flowConfigMapper.selectById(flowConfig.getFlowId());
    }

    @Override
    public void delete(Long flowId) {
        FlowConfig existing = flowConfigMapper.selectById(flowId);
        if (existing == null) {
            throw new BusinessException(404, "流程配置不存在");
        }
        flowConfigMapper.deleteById(flowId);
    }

    @Override
    public List<FlowConfig> list(String flowName, Long deptId) {
        LambdaQueryWrapper<FlowConfig> wrapper = new LambdaQueryWrapper<FlowConfig>()
                .like(StringUtils.hasText(flowName), FlowConfig::getFlowName, flowName)
                .eq(deptId != null, FlowConfig::getDeptId, deptId)
                .orderByDesc(FlowConfig::getCreateTime);
        return flowConfigMapper.selectList(wrapper);
    }
}
