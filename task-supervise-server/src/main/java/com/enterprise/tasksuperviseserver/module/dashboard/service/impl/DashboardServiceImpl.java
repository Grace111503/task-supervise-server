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

import static com.enterprise.tasksuperviseserver.common.constant.TaskConstant.*;

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

    /** 六大状态定义：状态码 → 数据库字符串 → 中文标签 */
    private static final String[][] STATUS_CARDS = {
            {STATUS_STR_PENDING, "待接收"},
            {STATUS_STR_IN_PROGRESS, "进行中"},
            {STATUS_STR_PENDING_FEEDBACK, "待反馈"},
            {STATUS_STR_PENDING_ACCEPT, "待验收"},
            {STATUS_STR_COMPLETED, "已完成"},
            {STATUS_STR_OVERDUE, "已逾期"}
    };

    @Override
    public List<Map<String, Object>> cards() {
        Long userId = UserContext.getUserId();
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < STATUS_CARDS.length; i++) {
            String statusStr = STATUS_CARDS[i][0];
            String label = STATUS_CARDS[i][1];
            long count = taskMapper.selectCount(buildMyTaskWrapper(userId)
                    .eq(Task::getStatus, statusStr));

            Map<String, Object> map = new HashMap<>();
            map.put("status", i + 1);
            map.put("label", label);
            map.put("count", count);
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> taskByStatus(Integer status, long page, long pageSize) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Task> wrapper = buildMyTaskWrapper(userId);
        if (status != null && status >= 1 && status <= STATUS_CARDS.length) {
            wrapper.eq(Task::getStatus, STATUS_CARDS[status - 1][0]);
        }
        wrapper.orderByDesc(Task::getCreatedAt);

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
                        .or().inSql(Task::getId,
                                "SELECT task_id FROM task_assignee WHERE user_id = " + userId));
    }
}
