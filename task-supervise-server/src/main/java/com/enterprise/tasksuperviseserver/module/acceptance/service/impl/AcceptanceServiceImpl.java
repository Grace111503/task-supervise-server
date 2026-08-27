package com.enterprise.tasksuperviseserver.module.acceptance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.Acceptance;
import com.enterprise.tasksuperviseserver.module.acceptance.mapper.AcceptanceMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.service.AcceptanceService;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 验收 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class AcceptanceServiceImpl implements AcceptanceService {

    private final AcceptanceMapper acceptanceMapper;
    private final TaskMapper taskMapper;

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
    public Acceptance add(Acceptance acceptance) {
        acceptance.setApplicantId(UserContext.getUserId());
        acceptance.setApplyTime(LocalDateTime.now());
        acceptance.setResult(TaskConstant.ACCEPT_RESULT_PENDING);
        acceptanceMapper.insert(acceptance);
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
    public Acceptance verify(Long acceptId, Integer result, String opinion) {
        Acceptance acceptance = approve(acceptId, result, opinion);
        // 核验通过（ACCEPT_RESULT_PASS = 1）→ 更新 task 状态为已完成
        if (Integer.valueOf(TaskConstant.ACCEPT_RESULT_PASS).equals(result) && acceptance.getTaskId() != null) {
            Task task = taskMapper.selectById(acceptance.getTaskId());
            if (task != null && !"completed".equals(task.getStatus())) {
                task.setStatus("completed");
                task.setUpdatedAt(LocalDateTime.now());
                taskMapper.updateById(task);
            }
        }
        return acceptance;
    }
}
