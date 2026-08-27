package com.enterprise.tasksuperviseserver.module.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskAssigneeMapper;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import com.enterprise.tasksuperviseserver.module.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final TaskAssigneeMapper taskAssigneeMapper;

    @Override
    public Map<String, Object> list(long page, long pageSize, Integer status, Integer priority,
                                     Long groupId, String keyword, Long assigneeId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Task::getStatus, status);
        }
        if (priority != null) {
            wrapper.eq(Task::getPriority, priority);
        }
        if (groupId != null) {
            wrapper.eq(Task::getGroupId, groupId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Task::getTitle, keyword)
                    .or().like(Task::getContent, keyword));
        }
        if (assigneeId != null) {
            wrapper.inSql(Task::getTaskId,
                    "SELECT task_id FROM task_assignee WHERE user_id = " + assigneeId);
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

    @Override
    public Task getDetail(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        return task;
    }

    @Override
    public Task create(Task task) {
        Long creatorId = UserContext.getUserId();
        task.setCreatorId(creatorId);
        task.setStatus(TaskConstant.STATUS_PENDING_RECEIVE);
        if (task.getAssigneeMode() == null) {
            task.setAssigneeMode(TaskConstant.ASSIGNEE_MODE_SINGLE);
        }
        LocalDateTime now = LocalDateTime.now();
        task.setCreateTime(now);
        task.setUpdateTime(now);
        taskMapper.insert(task);
        return task;
    }

    @Override
    public Task update(Task task) {
        if (task.getTaskId() == null) {
            throw new BusinessException("任务ID不能为空");
        }
        Task existing = taskMapper.selectById(task.getTaskId());
        if (existing == null) {
            throw new BusinessException(404, "任务不存在");
        }
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.updateById(task);
        return taskMapper.selectById(task.getTaskId());
    }

    @Override
    public boolean delete(Long taskId) {
        Task existing = taskMapper.selectById(taskId);
        if (existing == null) {
            throw new BusinessException(404, "任务不存在");
        }
        return taskMapper.deleteById(taskId) > 0;
    }

    @Override
    public void assign(Long taskId, Long assigneeId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        TaskAssignee assignee = new TaskAssignee();
        assignee.setTaskId(taskId);
        assignee.setUserId(assigneeId);
        assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_PRIMARY);
        assignee.setReceiveTime(LocalDateTime.now());
        taskAssigneeMapper.insert(assignee);

        if (task.getStatus() != null && task.getStatus() == TaskConstant.STATUS_PENDING_RECEIVE) {
            task.setStatus(TaskConstant.STATUS_IN_PROGRESS);
            task.setUpdateTime(LocalDateTime.now());
            taskMapper.updateById(task);
        }
    }

    @Override
    public void updateStatus(Long taskId, Integer status) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        task.setStatus(status);
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    @Override
    public Map<String, Object> statistics() {
        Long userId = UserContext.getUserId();

        LambdaQueryWrapper<Task> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.and(w -> w.eq(Task::getCreatorId, userId)
                .or().inSql(Task::getTaskId,
                        "SELECT task_id FROM task_assignee WHERE user_id = " + userId));

        long total = taskMapper.selectCount(baseWrapper);

        long pending = taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                .eq(Task::getStatus, TaskConstant.STATUS_PENDING_RECEIVE)
                .and(w -> w.eq(Task::getCreatorId, userId)
                        .or().inSql(Task::getTaskId,
                                "SELECT task_id FROM task_assignee WHERE user_id = " + userId)));

        long inProgress = taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                .eq(Task::getStatus, TaskConstant.STATUS_IN_PROGRESS)
                .and(w -> w.eq(Task::getCreatorId, userId)
                        .or().inSql(Task::getTaskId,
                                "SELECT task_id FROM task_assignee WHERE user_id = " + userId)));

        long completed = taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                .eq(Task::getStatus, TaskConstant.STATUS_COMPLETED)
                .and(w -> w.eq(Task::getCreatorId, userId)
                        .or().inSql(Task::getTaskId,
                                "SELECT task_id FROM task_assignee WHERE user_id = " + userId)));

        long overdue = taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                .eq(Task::getStatus, TaskConstant.STATUS_OVERDUE)
                .and(w -> w.eq(Task::getCreatorId, userId)
                        .or().inSql(Task::getTaskId,
                                "SELECT task_id FROM task_assignee WHERE user_id = " + userId)));

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("pending", pending);
        map.put("inProgress", inProgress);
        map.put("completed", completed);
        map.put("overdue", overdue);
        return map;
    }

    @Override
    public Map<String, Object> batchImport(byte[] bytes) {
        Long creatorId = UserContext.getUserId();
        int success = 0;
        int fail = 0;

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            // 跳过表头行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                try {
                    Task task = new Task();
                    task.setTitle(getStringCell(row.getCell(0)));
                    task.setContent(getStringCell(row.getCell(1)));
                    task.setPriority(getIntCell(row.getCell(2), TaskConstant.PRIORITY_NORMAL));
                    task.setCreatorId(creatorId);
                    task.setStatus(TaskConstant.STATUS_PENDING_RECEIVE);
                    task.setAssigneeMode(TaskConstant.ASSIGNEE_MODE_SINGLE);
                    LocalDateTime now = LocalDateTime.now();
                    task.setCreateTime(now);
                    task.setUpdateTime(now);

                    if (!StringUtils.hasText(task.getTitle())) {
                        fail++;
                        continue;
                    }
                    taskMapper.insert(task);
                    success++;
                } catch (Exception e) {
                    fail++;
                }
            }
        } catch (Exception e) {
            throw new BusinessException("Excel 文件解析失败: " + e.getMessage());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("fail", fail);
        map.put("total", success + fail);
        return map;
    }

    @Override
    public Map<String, Object> batchAssign(List<Long> taskIds, Long assigneeId) {
        if (taskIds == null || taskIds.isEmpty()) {
            throw new BusinessException("任务ID列表不能为空");
        }
        if (assigneeId == null) {
            throw new BusinessException("被指派人ID不能为空");
        }

        int success = 0;
        int fail = 0;

        for (Long taskId : taskIds) {
            try {
                Task task = taskMapper.selectById(taskId);
                if (task == null) {
                    fail++;
                    continue;
                }
                TaskAssignee assignee = new TaskAssignee();
                assignee.setTaskId(taskId);
                assignee.setUserId(assigneeId);
                assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_PRIMARY);
                assignee.setReceiveTime(LocalDateTime.now());
                taskAssigneeMapper.insert(assignee);

                if (task.getStatus() != null && task.getStatus() == TaskConstant.STATUS_PENDING_RECEIVE) {
                    task.setStatus(TaskConstant.STATUS_IN_PROGRESS);
                    task.setUpdateTime(LocalDateTime.now());
                    taskMapper.updateById(task);
                }
                success++;
            } catch (Exception e) {
                fail++;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("fail", fail);
        map.put("total", success + fail);
        return map;
    }

    /**
     * 读取字符串单元格
     */
    private String getStringCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return null;
    }

    /**
     * 读取整数单元格
     */
    private int getIntCell(Cell cell, int defaultValue) {
        if (cell == null) {
            return defaultValue;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
