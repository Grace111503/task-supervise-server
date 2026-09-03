package com.enterprise.tasksuperviseserver.module.acceptance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.OverdueAccountability;
import com.enterprise.tasksuperviseserver.module.acceptance.mapper.OverdueAccountabilityMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.service.OverdueAccountabilityService;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskAssigneeMapper;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import com.enterprise.tasksuperviseserver.module.warn.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 逾期问责 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OverdueAccountabilityServiceImpl implements OverdueAccountabilityService {

    private final OverdueAccountabilityMapper overdueAccountabilityMapper;
    private final TaskMapper taskMapper;
    private final TaskAssigneeMapper taskAssigneeMapper;

    @Autowired(required = false)
    private NotificationService notificationService;

    @Override
    public Page<OverdueAccountability> page(int pageNo, int pageSize) {
        Page<OverdueAccountability> page = Page.of(pageNo, pageSize);
        LambdaQueryWrapper<OverdueAccountability> wrapper = new LambdaQueryWrapper<OverdueAccountability>()
                .orderByDesc(OverdueAccountability::getArchiveTime);
        return overdueAccountabilityMapper.selectPage(page, wrapper);
    }

    @Override
    public OverdueAccountability getDetail(Long accountabilityId) {
        OverdueAccountability accountability = overdueAccountabilityMapper.selectById(accountabilityId);
        if (accountability == null) {
            throw new BusinessException(404, "逾期问责记录不存在");
        }
        return accountability;
    }

    @Override
    public OverdueAccountability add(OverdueAccountability accountability) {
        accountability.setArchiveTime(LocalDateTime.now());
        overdueAccountabilityMapper.insert(accountability);
        return accountability;
    }

    @Override
    public OverdueAccountability update(OverdueAccountability accountability) {
        if (accountability.getAccountabilityId() == null) {
            throw new BusinessException(400, "问责ID不能为空");
        }
        OverdueAccountability existing = overdueAccountabilityMapper.selectById(accountability.getAccountabilityId());
        if (existing == null) {
            throw new BusinessException(404, "逾期问责记录不存在");
        }
        overdueAccountabilityMapper.updateById(accountability);
        return overdueAccountabilityMapper.selectById(accountability.getAccountabilityId());
    }

    @Override
    public void delete(Long accountabilityId) {
        OverdueAccountability existing = overdueAccountabilityMapper.selectById(accountabilityId);
        if (existing == null) {
            throw new BusinessException(404, "逾期问责记录不存在");
        }
        overdueAccountabilityMapper.deleteById(accountabilityId);
    }

    @Override
    public List<OverdueAccountability> listByTaskId(Long taskId) {
        LambdaQueryWrapper<OverdueAccountability> wrapper = new LambdaQueryWrapper<OverdueAccountability>()
                .eq(OverdueAccountability::getTaskId, taskId)
                .orderByDesc(OverdueAccountability::getArchiveTime);
        return overdueAccountabilityMapper.selectList(wrapper);
    }

    @Override
    public OverdueAccountability recordReason(Long taskId, String reason, Integer overdueDays) {
        // 先查询是否已有该任务的记录，有则更新，无则新建
        OverdueAccountability existing = overdueAccountabilityMapper.selectOne(
                new LambdaQueryWrapper<OverdueAccountability>()
                        .eq(OverdueAccountability::getTaskId, taskId)
                        .last("LIMIT 1"));
        OverdueAccountability result;
        if (existing != null) {
            existing.setReason(reason);
            if (overdueDays != null) {
                existing.setOverdueDays(overdueDays);
            }
            overdueAccountabilityMapper.updateById(existing);
            result = overdueAccountabilityMapper.selectById(existing.getAccountabilityId());
        } else {
            OverdueAccountability a = new OverdueAccountability();
            a.setTaskId(taskId);
            a.setReason(reason);
            a.setOverdueDays(overdueDays);
            result = add(a);
        }
        // 通知执行人
        notifyAssignees(taskId, "📝 逾期原因已登记",
                String.format("任务「%s」的逾期原因已登记，请查看", getTaskTitle(taskId)));
        return result;
    }

    @Override
    public OverdueAccountability recordAccountability(Long taskId, String disposition, Integer overdueDays) {
        // 先查询是否已有该任务的记录，有则更新，无则新建
        OverdueAccountability existing = overdueAccountabilityMapper.selectOne(
                new LambdaQueryWrapper<OverdueAccountability>()
                        .eq(OverdueAccountability::getTaskId, taskId)
                        .last("LIMIT 1"));
        OverdueAccountability result;
        if (existing != null) {
            existing.setDisposition(disposition);
            if (overdueDays != null) {
                existing.setOverdueDays(overdueDays);
            }
            overdueAccountabilityMapper.updateById(existing);
            result = overdueAccountabilityMapper.selectById(existing.getAccountabilityId());
        } else {
            OverdueAccountability a = new OverdueAccountability();
            a.setTaskId(taskId);
            a.setDisposition(disposition);
            a.setOverdueDays(overdueDays);
            result = add(a);
        }
        // 通知执行人
        notifyAssignees(taskId, "⚖️ 追责处置已登记",
                String.format("任务「%s」的追责处置措施已登记，请查看", getTaskTitle(taskId)));
        return result;
    }

    /**
     * 获取任务标题
     */
    private String getTaskTitle(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        return task != null ? task.getTitle() : "未知任务";
    }

    /**
     * 通知任务所有执行人
     */
    private void notifyAssignees(Long taskId, String title, String content) {
        if (notificationService == null) return;
        try {
            List<TaskAssignee> assignees = taskAssigneeMapper.selectList(
                    new LambdaQueryWrapper<TaskAssignee>().eq(TaskAssignee::getTaskId, taskId));
            for (TaskAssignee assignee : assignees) {
                try {
                    notificationService.sendNotification(
                            assignee.getUserId(), title, content, 2, "WARN", taskId);
                } catch (Exception e) {
                    log.debug("通知执行人失败: userId={}, error={}", assignee.getUserId(), e.getMessage());
                }
            }
            log.info("逾期处置通知已发送: taskId={}, 执行人数={}", taskId, assignees.size());
        } catch (Exception e) {
            log.warn("发送逾期处置通知失败: taskId={}, error={}", taskId, e.getMessage());
        }
    }
}
