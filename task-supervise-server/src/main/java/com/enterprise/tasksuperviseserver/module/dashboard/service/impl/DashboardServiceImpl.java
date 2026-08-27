package com.enterprise.tasksuperviseserver.module.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.module.dashboard.service.DashboardService;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 状态跟踪看板 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TaskMapper taskMapper;

    /** 六大状态定义：status → label */
    private static final int[][] STATUS_CARDS = {
            {TaskConstant.STATUS_PENDING_RECEIVE, 1},
            {TaskConstant.STATUS_IN_PROGRESS, 2},
            {TaskConstant.STATUS_PENDING_FEEDBACK, 3},
            {TaskConstant.STATUS_PENDING_ACCEPT, 4},
            {TaskConstant.STATUS_COMPLETED, 5},
            {TaskConstant.STATUS_OVERDUE, 6}
    };

    private static final String[] STATUS_LABELS = {
            "", "待接收", "进行中", "待反馈", "待验收", "已完成", "已逾期"
    };

    @Override
    public List<Map<String, Object>> cards() {
        Long userId = UserContext.getUserId();
        List<Map<String, Object>> result = new ArrayList<>();

        for (int[] card : STATUS_CARDS) {
            int statusVal = card[0];
            long count = taskMapper.selectCount(buildMyTaskWrapper(userId)
                    .eq(Task::getStatus, statusVal));

            Map<String, Object> map = new HashMap<>();
            map.put("status", statusVal);
            map.put("label", STATUS_LABELS[statusVal]);
            map.put("count", count);
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> taskByStatus(Integer status, long page, long pageSize) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Task> wrapper = buildMyTaskWrapper(userId);
        if (status != null) {
            wrapper.eq(Task::getStatus, status);
        }
        wrapper.orderByDesc(Task::getCreateTime);

        Page<Task> result = taskMapper.selectPage(Page.of(page, pageSize), wrapper);

        Map<String, Object> map = new HashMap<>();
        map.put("list", result.getRecords());
        map.put("page", result.getCurrent());
        map.put("pageSize", result.getSize());
        map.put("total", result.getTotal());
        return map;
    }

    /**
     * 构建"我相关的任务"查询条件：我创建的 或 指派给我的
     */
    private LambdaQueryWrapper<Task> buildMyTaskWrapper(Long userId) {
        if (userId == null) {
            return new LambdaQueryWrapper<>();
        }
        return new LambdaQueryWrapper<Task>()
                .and(w -> w.eq(Task::getCreatorId, userId)
                        .or().inSql(Task::getTaskId,
                                "SELECT task_id FROM task_assignee WHERE user_id = " + userId));
    }
}
