package com.enterprise.tasksuperviseserver.module.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskTemplate;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskAssigneeMapper;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import com.enterprise.tasksuperviseserver.module.task.service.TaskCacheService;
import com.enterprise.tasksuperviseserver.module.task.service.TaskService;
import com.enterprise.tasksuperviseserver.module.task.service.TaskTemplateService;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysUserMapper;
import com.enterprise.tasksuperviseserver.module.warn.service.NotificationService;
import com.enterprise.tasksuperviseserver.module.task.vo.TaskListItemVO;
import com.enterprise.tasksuperviseserver.module.feedback.entity.ProgressFeedback;
import com.enterprise.tasksuperviseserver.module.feedback.mapper.ProgressFeedbackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 任务 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final TaskAssigneeMapper taskAssigneeMapper;
    private final TaskCacheService taskCacheService;
    private final TaskTemplateService taskTemplateService;
    private final ProgressFeedbackMapper progressFeedbackMapper;
    private final SysUserMapper sysUserMapper;
    private final NotificationService notificationService;

    /** 状态映射：前端整数 → 数据库字符串 */
    private static final Map<Integer, String> STATUS_MAP = Map.of(
            1, "pending",           // 待接收
            2, "in_progress",       // 进行中
            3, "pending_feedback",  // 待反馈
            4, "pending_accept",    // 待验收
            5, "completed",         // 已完成
            6, "overdue"            // 已逾期
    );

    /** 优先级映射：前端整数 → 数据库字符串 */
    private static final Map<Integer, String> PRIORITY_MAP = Map.of(
            1, "medium",   // 普通
            2, "high",     // 重要
            3, "low"       // 紧急(相对)
    );

    /** 角色中文描述映射 */
    private static final Map<String, String> ROLE_DESC_MAP = Map.of(
            "admin", "督办管理员",
            "manager", "部门主管",
            "user", "普通执行人员"
    );

    @Override
    public Map<String, Object> list(long page, long pageSize, Integer status, Integer priority,
                                     Long groupId, String keyword, Long assigneeId) {
        Long currentUserId = UserContext.getUserId();
        String currentRole = UserContext.getRole();
        Long currentDeptId = UserContext.getDeptId();

        String cacheKey = TaskCacheService.buildKey(currentUserId, page, pageSize,
                status, priority, groupId, keyword, assigneeId);

        // 先查 Redis 缓存
        Map<String, Object> cached = taskCacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();

        // 多人协办子查询：task_assignee 表中当前用户参与的任务
        String multiAssigneeSql = "SELECT task_id FROM task_assignee WHERE user_id = " + currentUserId;

        // ===== 三级权限数据过滤 =====
        if ("user".equals(currentRole)) {
            // 普通执行人员：仅查看指派给我的(单人+多人) + 我创建的
            wrapper.and(w -> w.eq(Task::getAssigneeId, currentUserId)
                    .or().inSql(Task::getId, multiAssigneeSql)
                    .or().eq(Task::getCreatorId, currentUserId));
        } else if ("manager".equals(currentRole)) {
            // 部门主管：查看本部门所有任务 + 我创建的 + 指派给我的(单人+多人)
            if (currentDeptId != null) {
                wrapper.and(w -> w.eq(Task::getDeptId, currentDeptId)
                        .or().eq(Task::getCreatorId, currentUserId)
                        .or().eq(Task::getAssigneeId, currentUserId)
                        .or().inSql(Task::getId, multiAssigneeSql));
            } else {
                // 没有部门信息时退化为个人视角
                wrapper.and(w -> w.eq(Task::getAssigneeId, currentUserId)
                        .or().inSql(Task::getId, multiAssigneeSql)
                        .or().eq(Task::getCreatorId, currentUserId));
            }
        }
        // admin 管理员：可看全部，不做过滤

        // 状态过滤：前端整数转数据库字符串
        if (status != null && STATUS_MAP.containsKey(status)) {
            wrapper.eq(Task::getStatus, STATUS_MAP.get(status));
        }
        // 优先级过滤：前端整数转数据库字符串
        if (priority != null && PRIORITY_MAP.containsKey(priority)) {
            wrapper.eq(Task::getPriority, PRIORITY_MAP.get(priority));
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Task::getTitle, keyword)
                    .or().like(Task::getDescription, keyword));
        }
        if (assigneeId != null) {
            wrapper.inSql(Task::getId,
                    "SELECT task_id FROM task_assignee WHERE user_id = " + assigneeId);
        }
        wrapper.orderByDesc(Task::getCreatedAt);

        Page<Task> result = taskMapper.selectPage(Page.of(page, pageSize), wrapper);

        // 转换为 VO，附加计算字段
        List<TaskListItemVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        Map<String, Object> map = new HashMap<>();
        map.put("list", voList);
        map.put("page", result.getCurrent());
        map.put("pageSize", result.getSize());
        map.put("total", result.getTotal());

        // 写入 Redis 缓存
        taskCacheService.put(cacheKey, map);
        return map;
    }

    @Override
    public Task getDetail(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        // 多人协办模式：填充执行人ID列表
        if (task.getAssigneeMode() != null && task.getAssigneeMode() == TaskConstant.ASSIGNEE_MODE_MULTI) {
            try {
                LambdaQueryWrapper<TaskAssignee> aw = new LambdaQueryWrapper<TaskAssignee>()
                        .eq(TaskAssignee::getTaskId, task.getId())
                        .select(TaskAssignee::getUserId);
                List<Long> userIds = taskAssigneeMapper.selectList(aw).stream()
                        .map(TaskAssignee::getUserId)
                        .toList();
                task.setMultiAssigneeIds(userIds);
            } catch (Exception e) {
                log.warn("[getDetail] 查询多人协办执行人失败, taskId={}: {}", task.getId(), e.getMessage());
            }
        }
        return task;
    }

    @Override
    public Task create(Task task) {
        Long creatorId = UserContext.getUserId();
        String creatorName = UserContext.getName() != null ? UserContext.getName() : UserContext.getUsername();
        task.setCreatorId(creatorId);
        task.setCreatorName(creatorName);
        if (task.getStatus() == null) {
            task.setStatus("pending");
        }
        if (task.getDeleted() == null) {
            task.setDeleted(0);
        }
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        // 多人协办模式：创建时直接设置 assigneeMode
        boolean isMultiMode = task.getAssigneeMode() != null
                && task.getAssigneeMode() == TaskConstant.ASSIGNEE_MODE_MULTI;
        java.util.List<Long> assigneeIds = task.getAssigneeIds();
        Long primaryId = task.getTransientPrimaryId();

        // [DEBUG] 打印接收到的任务数据
        log.info("[create] title={}, assigneeMode={}, isMultiMode={}, assigneeIds={}, primaryId={}",
                task.getTitle(), task.getAssigneeMode(), isMultiMode, assigneeIds, primaryId);

        // 单人模式：自动填充执行人姓名
        if (!isMultiMode && task.getAssigneeId() != null) {
            SysUser assigneeUser = sysUserMapper.selectById(task.getAssigneeId());
            if (assigneeUser != null) {
                String aName = assigneeUser.getName() != null ? assigneeUser.getName() : assigneeUser.getUserName();
                task.setAssigneeName(aName);
            }
        }

        taskMapper.insert(task);

        // 多人协办：在同一个事务中写入 task_assignee 记录
        if (isMultiMode && assigneeIds != null && !assigneeIds.isEmpty()) {
            // 批量查询执行人信息
            Map<Long, SysUser> userMap = new HashMap<>();
            for (Long uid : assigneeIds) {
                SysUser u = sysUserMapper.selectById(uid);
                if (u != null) userMap.put(uid, u);
            }

            for (Long userId : assigneeIds) {
                TaskAssignee assignee = new TaskAssignee();
                assignee.setTaskId(task.getId());
                assignee.setUserId(userId);
                // 设置执行人真实姓名
                SysUser assigneeUser = userMap.get(userId);
                if (assigneeUser != null) {
                    String aName = assigneeUser.getName() != null ? assigneeUser.getName() : assigneeUser.getUserName();
                    assignee.setAssigneeName(aName);
                }
                if (primaryId != null && primaryId.equals(userId)) {
                    assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_PRIMARY);
                } else if (primaryId == null && userId.equals(assigneeIds.get(0))) {
                    assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_PRIMARY);
                } else {
                    assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_ASSIST);
                }
                assignee.setStatus("pending");
                assignee.setCreatedAt(now);
                taskAssigneeMapper.insert(assignee);
            }
            // 多人任务保持 pending 状态，等执行人真正开始处理时再变为 in_progress
        }

        // 清除所有缓存（新任务可能影响多个用户的可见性）
        taskCacheService.evictAll();

        // ===== 通知执行人有新任务 =====
        try {
            String notifyTitle = "📋 新任务分配";
            String notifyContent = String.format("您有新任务「%s」，请及时处理", task.getTitle());
            if (isMultiMode && assigneeIds != null) {
                for (Long uid : assigneeIds) {
                    if (!uid.equals(creatorId)) {
                        notificationService.sendNotification(uid, notifyTitle, notifyContent,
                                1, TaskConstant.MSG_TYPE_TASK, task.getId());
                    }
                }
            } else if (task.getAssigneeId() != null && !task.getAssigneeId().equals(creatorId)) {
                notificationService.sendNotification(task.getAssigneeId(), notifyTitle, notifyContent,
                        1, TaskConstant.MSG_TYPE_TASK, task.getId());
            }
        } catch (Exception e) {
            log.warn("发送任务分配通知失败: {}", e.getMessage());
        }

        return task;
    }

    @Override
    public Task update(Task task) {
        if (task.getId() == null) {
            throw new BusinessException("任务ID不能为空");
        }
        Task existing = taskMapper.selectById(task.getId());
        if (existing == null) {
            throw new BusinessException(404, "任务不存在");
        }
        // 权限检查：普通执行人员只能编辑自己创建的任务
        checkEditPermission(existing);
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        taskCacheService.evictAll();
        return taskMapper.selectById(task.getId());
    }

    @Override
    public boolean delete(Long taskId) {
        Task existing = taskMapper.selectById(taskId);
        if (existing == null) {
            throw new BusinessException(404, "任务不存在");
        }
        // 权限检查：普通执行人员只能删除已完成的任务
        checkDeletePermission(existing);
        boolean ok = taskMapper.deleteById(taskId) > 0;
        taskCacheService.evictAll();
        return ok;
    }

    /**
     * 编辑权限检查
     * - admin：可编辑所有任务
     * - manager：可编辑本部门任务或自己创建的任务
     * - user（普通执行人员）：只能编辑自己创建的任务
     */
    private void checkEditPermission(Task task) {
        String role = UserContext.getRole();
        Long userId = UserContext.getUserId();

        if ("admin".equals(role)) {
            return; // 管理员不限制
        }
        if ("manager".equals(role)) {
            // 主管：自己创建的，或本部门的任务
            if (task.getCreatorId() != null && task.getCreatorId().equals(userId)) {
                return;
            }
            if (task.getDeptId() != null && task.getDeptId().equals(UserContext.getDeptId())) {
                return;
            }
            throw new BusinessException(403, "无权操作此任务");
        }
        // 普通执行人员：只能编辑自己创建的任务
        if (task.getCreatorId() != null && task.getCreatorId().equals(userId)) {
            return;
        }
        throw new BusinessException(403, "普通执行人员只能编辑自己创建的任务");
    }

    /**
     * 删除权限检查
     * - admin：可删除所有任务
     * - manager：可删除本部门任务或自己创建的任务
     * - user（普通执行人员）：自己创建的任务可删除；分配给自己的任务仅已完成可删除
     */
    private void checkDeletePermission(Task task) {
        String role = UserContext.getRole();
        Long userId = UserContext.getUserId();

        if ("admin".equals(role)) {
            return; // 管理员不限制
        }
        if ("manager".equals(role)) {
            // 主管：自己创建的，或本部门的任务
            if (task.getCreatorId() != null && task.getCreatorId().equals(userId)) {
                return;
            }
            if (task.getDeptId() != null && task.getDeptId().equals(UserContext.getDeptId())) {
                return;
            }
            throw new BusinessException(403, "无权删除此任务");
        }
        // 普通执行人员：自己创建的任务拥有完整权限
        if (task.getCreatorId() != null && task.getCreatorId().equals(userId)) {
            return;
        }
        // 分配给自己的任务（单人模式）：仅已完成可删除
        if (task.getAssigneeId() != null && task.getAssigneeId().equals(userId)) {
            if (!"completed".equals(task.getStatus())) {
                throw new BusinessException(403, "任务未完成前不能删除");
            }
            return;
        }
        // 多人协办任务：检查 task_assignee 表
        if (task.getAssigneeMode() != null && task.getAssigneeMode() == TaskConstant.ASSIGNEE_MODE_MULTI) {
            LambdaQueryWrapper<TaskAssignee> aw = new LambdaQueryWrapper<TaskAssignee>()
                    .eq(TaskAssignee::getTaskId, task.getId())
                    .eq(TaskAssignee::getUserId, userId);
            Long count = taskAssigneeMapper.selectCount(aw);
            if (count != null && count > 0) {
                if (!"completed".equals(task.getStatus())) {
                    throw new BusinessException(403, "任务未完成前不能删除");
                }
                return;
            }
        }
        throw new BusinessException(403, "无权删除此任务");
    }

    @Override
    public void assign(Long taskId, Long assigneeId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        // 查询执行人真实姓名
        SysUser assigneeUser = sysUserMapper.selectById(assigneeId);
        String assigneeName = null;
        if (assigneeUser != null) {
            assigneeName = assigneeUser.getName() != null ? assigneeUser.getName() : assigneeUser.getUserName();
        }

        TaskAssignee assignee = new TaskAssignee();
        assignee.setTaskId(taskId);
        assignee.setUserId(assigneeId);
        assignee.setAssigneeName(assigneeName);
        assignee.setStatus("pending");
        assignee.setCreatedAt(LocalDateTime.now());
        taskAssigneeMapper.insert(assignee);

        if ("pending".equals(task.getStatus())) {
            task.setStatus("in_progress");
            task.setUpdatedAt(LocalDateTime.now());
            taskMapper.updateById(task);
        }
        taskCacheService.evictAll();
    }

    @Override
    public void updateStatus(Long taskId, Integer status) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        // Integer 状态码转 String
        String statusStr = convertStatusToString(status);
        // 标记完成需要权限检查
        checkCompletePermission(task, statusStr);
        // 执行人操作检查（开始处理等）
        checkAssigneePermission(task, statusStr);
        task.setStatus(statusStr);
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        taskCacheService.evictAll();
    }

    /** 支持字符串状态直接更新 */
    @Override
    public void updateStatus(Long taskId, String status) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        // 标记完成需要权限检查
        checkCompletePermission(task, status);
        // 执行人操作检查（开始处理等）
        checkAssigneePermission(task, status);
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        taskCacheService.evictAll();
    }

    /**
     * 执行人操作检查
     * pending → in_progress（开始处理）仅执行人可操作
     * 提交反馈仅执行人可操作
     */
    private void checkAssigneePermission(Task task, String newStatus) {
        // 仅检查需要执行人身份的操作
        if (!"in_progress".equals(newStatus)) {
            return;
        }
        // 管理员和主管不受限
        String role = UserContext.getRole();
        if ("admin".equals(role) || "manager".equals(role)) {
            return;
        }
        Long userId = UserContext.getUserId();
        // 创建人不能执行"开始处理"
        if (task.getCreatorId() != null && task.getCreatorId().equals(userId)) {
            throw new BusinessException(403, "任务发布者不能执行此操作");
        }
    }

    /**
     * 标记完成权限检查
     * 普通用户不能将未完成的任务标记为完成，只有创建人/管理员/主管可以
     */
    private void checkCompletePermission(Task task, String newStatus) {
        if (!"completed".equals(newStatus)) {
            return; // 非完成操作不拦截
        }
        String role = UserContext.getRole();
        Long userId = UserContext.getUserId();

        if ("admin".equals(role) || "manager".equals(role)) {
            return; // 管理员和主管可以标记完成
        }
        // 普通用户：只有创建人可以标记完成
        if (task.getCreatorId() != null && task.getCreatorId().equals(userId)) {
            return;
        }
        throw new BusinessException(403, "普通用户不能标记任务完成");
    }

    /** 状态整数转字符串 */
    private String convertStatusToString(Integer status) {
        if (status == null) return "pending";
        return STATUS_MAP.getOrDefault(status, "pending");
    }

    @Override
    public Map<String, Object> statistics() {
        Long userId = UserContext.getUserId();
        String currentRole = UserContext.getRole();
        Long deptId = UserContext.getDeptId();

        // 构建基础过滤条件（与 list 方法保持一致）
        LambdaQueryWrapper<Task> baseWrapper = new LambdaQueryWrapper<>();
        applyRoleFilter(baseWrapper, currentRole, userId, deptId);

        long total = taskMapper.selectCount(baseWrapper);

        // 统计六状态数量
        long pendingReceive = countByStatus(currentRole, userId, deptId, "pending");
        long inProgress = countByStatus(currentRole, userId, deptId, "in_progress");
        long pendingFeedback = countByStatus(currentRole, userId, deptId, "pending_feedback");
        long pendingAccept = countByStatus(currentRole, userId, deptId, "pending_accept");
        long completed = countByStatus(currentRole, userId, deptId, "completed");
        long overdue = countByStatus(currentRole, userId, deptId, "overdue");

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("pendingReceive", pendingReceive);
        map.put("inProgress", inProgress);
        map.put("pendingFeedback", pendingFeedback);
        map.put("pendingAccept", pendingAccept);
        map.put("completed", completed);
        map.put("overdue", overdue);
        return map;
    }

    /** 根据角色应用过滤条件（包含多人协办任务） */
    private void applyRoleFilter(LambdaQueryWrapper<Task> wrapper, String role, Long userId, Long deptId) {
        // 多人协办子查询：task_assignee 表中当前用户参与的任务
        String multiAssigneeSql = "SELECT task_id FROM task_assignee WHERE user_id = " + userId;

        if ("user".equals(role)) {
            wrapper.and(w -> w.eq(Task::getAssigneeId, userId)
                    .or().inSql(Task::getId, multiAssigneeSql)
                    .or().eq(Task::getCreatorId, userId));
        } else if ("manager".equals(role)) {
            if (deptId != null) {
                wrapper.and(w -> w.eq(Task::getDeptId, deptId)
                        .or().eq(Task::getCreatorId, userId)
                        .or().eq(Task::getAssigneeId, userId)
                        .or().inSql(Task::getId, multiAssigneeSql));
            } else {
                wrapper.and(w -> w.eq(Task::getAssigneeId, userId)
                        .or().inSql(Task::getId, multiAssigneeSql)
                        .or().eq(Task::getCreatorId, userId));
            }
        }
        // admin 不过滤
    }

    /** 按状态统计数量 */
    private long countByStatus(String role, Long userId, Long deptId, String status) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        applyRoleFilter(wrapper, role, userId, deptId);
        wrapper.eq(Task::getStatus, status);
        return taskMapper.selectCount(wrapper);
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
                    task.setDescription(getStringCell(row.getCell(1)));
                    task.setPriority(getStringCell(row.getCell(2)));
                    task.setCreatorId(creatorId);
                    task.setStatus("pending");
                    task.setDeleted(0);
                    LocalDateTime now = LocalDateTime.now();
                    task.setCreatedAt(now);
                    task.setUpdatedAt(now);

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
    public void batchAssign(List<Long> taskIds, Long assigneeId) {
        if (taskIds == null || taskIds.isEmpty()) {
            throw new BusinessException("任务ID列表不能为空");
        }
        if (assigneeId == null) {
            throw new BusinessException("被指派人ID不能为空");
        }

        // 查询执行人真实姓名
        SysUser assigneeUser = sysUserMapper.selectById(assigneeId);
        String assigneeName = null;
        if (assigneeUser != null) {
            assigneeName = assigneeUser.getName() != null ? assigneeUser.getName() : assigneeUser.getUserName();
        }

        for (Long taskId : taskIds) {
            Task task = taskMapper.selectById(taskId);
            if (task == null) {
                continue;
            }
            TaskAssignee assignee = new TaskAssignee();
            assignee.setTaskId(taskId);
            assignee.setUserId(assigneeId);
            assignee.setAssigneeName(assigneeName);
            assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_PRIMARY);
            assignee.setStatus("pending");
            assignee.setCreatedAt(LocalDateTime.now());
            taskAssigneeMapper.insert(assignee);

            if ("pending".equals(task.getStatus())) {
                task.setStatus("in_progress");
                task.setAssigneeMode(TaskConstant.ASSIGNEE_MODE_SINGLE);
                task.setUpdatedAt(LocalDateTime.now());
                taskMapper.updateById(task);
            }
        }
        taskCacheService.evictAll();
    }

    @Override
    public void assignMulti(Long taskId, List<Long> assigneeIds, Long primaryId) {
        if (taskId == null) {
            throw new BusinessException("任务ID不能为空");
        }
        if (assigneeIds == null || assigneeIds.isEmpty()) {
            throw new BusinessException("指派人ID列表不能为空");
        }

        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }

        // 设置多人协办模式
        task.setAssigneeMode(TaskConstant.ASSIGNEE_MODE_MULTI);
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);

        // 批量查询执行人信息
        Map<Long, SysUser> userMap = new HashMap<>();
        for (Long uid : assigneeIds) {
            SysUser u = sysUserMapper.selectById(uid);
            if (u != null) userMap.put(uid, u);
        }

        // 批量添加指派人
        for (Long userId : assigneeIds) {
            TaskAssignee assignee = new TaskAssignee();
            assignee.setTaskId(taskId);
            assignee.setUserId(userId);
            // 设置执行人真实姓名
            SysUser assigneeUser = userMap.get(userId);
            if (assigneeUser != null) {
                String aName = assigneeUser.getName() != null ? assigneeUser.getName() : assigneeUser.getUserName();
                assignee.setAssigneeName(aName);
            }
            // 如果指定了主负责人，或者是指派人列表的第一个，则为主负责人
            if (primaryId != null && primaryId.equals(userId)) {
                assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_PRIMARY);
            } else if (primaryId == null && userId.equals(assigneeIds.get(0))) {
                assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_PRIMARY);
            } else {
                assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_ASSIST);
            }
            assignee.setStatus("pending");
            assignee.setCreatedAt(LocalDateTime.now());
            taskAssigneeMapper.insert(assignee);
        }

        // 更新任务状态
        if ("pending".equals(task.getStatus())) {
            task.setStatus("in_progress");
            taskMapper.updateById(task);
        }
        taskCacheService.evictAll();
    }

    @Override
    public Map<String, Object> batchAssignResult(List<Long> taskIds, Long assigneeId) {
        if (taskIds == null || taskIds.isEmpty()) {
            throw new BusinessException("任务ID列表不能为空");
        }
        if (assigneeId == null) {
            throw new BusinessException("被指派人ID不能为空");
        }

        int success = 0;
        int fail = 0;

        // 查询执行人真实姓名
        SysUser assigneeUser = sysUserMapper.selectById(assigneeId);
        String assigneeName = null;
        if (assigneeUser != null) {
            assigneeName = assigneeUser.getName() != null ? assigneeUser.getName() : assigneeUser.getUserName();
        }

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
                assignee.setAssigneeName(assigneeName);
                assignee.setAssigneeType(TaskConstant.ASSIGNEE_TYPE_PRIMARY);
                assignee.setStatus("pending");
                assignee.setCreatedAt(LocalDateTime.now());
                taskAssigneeMapper.insert(assignee);

                if ("pending".equals(task.getStatus())) {
                    task.setStatus("in_progress");
                    task.setUpdatedAt(LocalDateTime.now());
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

    @Override
    public Task createByTemplate(Long templateId, Task task) {
        if (templateId == null) {
            throw new BusinessException("模板ID不能为空");
        }

        TaskTemplate template = taskTemplateService.getDetail(templateId);
        if (template == null) {
            throw new BusinessException(404, "任务模板不存在");
        }

        // 使用模板默认值填充任务
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            task.setTitle(template.getTemplateName());
        }
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            task.setDescription(template.getDefaultContent());
        }
        if (task.getPriority() == null) {
            // 根据模板默认优先级转换
            switch (template.getDefaultPriority()) {
                case 2 -> task.setPriority("high");
                case 3 -> task.setPriority("low");
                default -> task.setPriority("medium");
            }
        }
        task.setTemplateId(templateId);

        return create(task);
    }

    @Override
    public Map<String, Object> listByGroup(Long groupId, long page, long pageSize) {
        if (groupId == null) {
            throw new BusinessException("任务组ID不能为空");
        }

        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getGroupId, groupId);
        wrapper.orderByDesc(Task::getCreatedAt);

        Page<Task> result = taskMapper.selectPage(Page.of(page, pageSize), wrapper);

        Map<String, Object> map = new HashMap<>();
        map.put("list", result.getRecords());
        map.put("page", result.getCurrent());
        map.put("pageSize", result.getSize());
        map.put("total", result.getTotal());
        return map;
    }

    @Override
    public void reject(Long taskId, String rejectRemark) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        // 权限检查：仅管理员和主管可驳回
        String role = UserContext.getRole();
        if (!"admin".equals(role) && !"manager".equals(role)) {
            throw new BusinessException(403, "仅管理员或主管可驳回任务");
        }
        // 只能驳回已完成或待验收的任务
        if (!"completed".equals(task.getStatus()) && !"pending_accept".equals(task.getStatus())) {
            throw new BusinessException("只能驳回已完成或待验收的任务");
        }
        task.setStatus("in_progress");
        task.setRejectRemark(rejectRemark);
        task.setRejectedAt(LocalDateTime.now());
        task.setAcceptResult(TaskConstant.ACCEPT_RESULT_REJECT);
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        taskCacheService.evictAll();
    }

    @Override
    public void accept(Long taskId, Integer acceptResult, String acceptRemark) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        String role = UserContext.getRole();
        if (!"admin".equals(role) && !"manager".equals(role)) {
            throw new BusinessException(403, "仅管理员或主管可验收任务");
        }
        if (!"pending_accept".equals(task.getStatus())) {
            throw new BusinessException("只能验收待验收状态的任务");
        }
        Long userId = UserContext.getUserId();
        task.setAcceptResult(acceptResult);
        task.setAcceptRemark(acceptRemark);
        task.setAcceptedAt(LocalDateTime.now());
        task.setAcceptedBy(userId);
        if (acceptResult == TaskConstant.ACCEPT_RESULT_PASS) {
            task.setStatus("completed");
        } else {
            // 驳回：回到进行中
            task.setStatus("in_progress");
            task.setRejectRemark(acceptRemark);
            task.setRejectedAt(LocalDateTime.now());
        }
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        taskCacheService.evictAll();

        // ===== 通知执行人验收结果 =====
        try {
            boolean passed = acceptResult == TaskConstant.ACCEPT_RESULT_PASS;
            String notifyTitle = passed ? "✅ 任务验收通过" : "❌ 任务验收驳回";
            String notifyContent = passed
                    ? String.format("任务「%s」已通过验收", task.getTitle())
                    : String.format("任务「%s」被驳回，原因：%s", task.getTitle(), acceptRemark);

            // 通知执行人（单人模式 + 多人模式）
            Set<Long> notifyIds = new HashSet<>();
            if (task.getAssigneeId() != null) {
                notifyIds.add(task.getAssigneeId());
            }
            // 多人模式：查询所有执行人
            List<TaskAssignee> assignees = taskAssigneeMapper.selectList(
                    new LambdaQueryWrapper<TaskAssignee>().eq(TaskAssignee::getTaskId, taskId));
            for (TaskAssignee a : assignees) {
                notifyIds.add(a.getUserId());
            }
            // 不通知操作人自己
            notifyIds.remove(userId);

            for (Long uid : notifyIds) {
                notificationService.sendNotification(uid, notifyTitle, notifyContent,
                        passed ? 1 : 2, TaskConstant.MSG_TYPE_ACCEPT, taskId);
            }
        } catch (Exception e) {
            log.warn("发送验收通知失败: {}", e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getTimeline(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        List<Map<String, Object>> timeline = new java.util.ArrayList<>();

        // 1. 创建任务
        Map<String, Object> createNode = new HashMap<>();
        createNode.put("type", "create");
        createNode.put("title", "创建任务");
        createNode.put("content", task.getTitle());
        createNode.put("operator", task.getCreatorName());
        createNode.put("time", task.getCreatedAt());
        timeline.add(createNode);

        // 2. 分派人员
        if (task.getAssigneeName() != null) {
            Map<String, Object> assignNode = new HashMap<>();
            assignNode.put("type", "assign");
            assignNode.put("title", "分派任务");
            assignNode.put("content", "指派给 " + task.getAssigneeName());
            assignNode.put("operator", task.getCreatorName());
            assignNode.put("time", task.getCreatedAt());
            timeline.add(assignNode);
        }

        // 3. 历史反馈
        LambdaQueryWrapper<ProgressFeedback> fbWrapper = new LambdaQueryWrapper<ProgressFeedback>()
                .eq(ProgressFeedback::getTaskId, taskId)
                .orderByAsc(ProgressFeedback::getStage);
        List<ProgressFeedback> feedbacks = progressFeedbackMapper.selectList(fbWrapper);
        for (ProgressFeedback fb : feedbacks) {
            Map<String, Object> fbNode = new HashMap<>();
            fbNode.put("type", "feedback");
            fbNode.put("title", "第" + fb.getStage() + "阶段反馈");
            fbNode.put("content", fb.getCompletedContent());
            fbNode.put("progress", fb.getProgressPercent());
            fbNode.put("operator", fb.getUserName());
            fbNode.put("time", fb.getFeedbackTime());
            timeline.add(fbNode);
        }

        // 4. 驳回记录
        if (task.getRejectRemark() != null) {
            Map<String, Object> rejectNode = new HashMap<>();
            rejectNode.put("type", "reject");
            rejectNode.put("title", "任务驳回");
            rejectNode.put("content", task.getRejectRemark());
            rejectNode.put("time", task.getRejectedAt());
            timeline.add(rejectNode);
        }

        // 5. 验收记录
        if (task.getAcceptResult() != null && task.getAcceptResult() != 0) {
            Map<String, Object> acceptNode = new HashMap<>();
            acceptNode.put("type", "accept");
            acceptNode.put("title", task.getAcceptResult() == 1 ? "验收通过" : "验收驳回");
            acceptNode.put("content", task.getAcceptRemark());
            acceptNode.put("time", task.getAcceptedAt());
            timeline.add(acceptNode);
        }

        // 按时间排序
        timeline.sort((a, b) -> {
            LocalDateTime tA = (LocalDateTime) a.get("time");
            LocalDateTime tB = (LocalDateTime) b.get("time");
            if (tA == null) return 1;
            if (tB == null) return -1;
            return tA.compareTo(tB);
        });

        return timeline;
    }

    @Override
    public Map<String, Object> overdueList(long page, long pageSize, Long groupId, Long assigneeId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getStatus, "overdue")
                .eq(Task::getDeleted, 0);
        if (groupId != null) {
            wrapper.eq(Task::getGroupId, groupId);
        }
        if (assigneeId != null) {
            wrapper.inSql(Task::getId,
                    "SELECT task_id FROM task_assignee WHERE user_id = " + assigneeId);
        }
        wrapper.orderByDesc(Task::getUpdatedAt);

        Page<Task> result = taskMapper.selectPage(Page.of(page, pageSize), wrapper);
        List<TaskListItemVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        Map<String, Object> map = new HashMap<>();
        map.put("list", voList);
        map.put("page", result.getCurrent());
        map.put("pageSize", result.getSize());
        map.put("total", result.getTotal());
        return map;
    }

    @Override
    public Map<String, Object> batchOverdueAction(List<Long> taskIds, String action, Map<String, Object> param) {
        String role = UserContext.getRole();
        if (!"admin".equals(role) && !"manager".equals(role)) {
            throw new BusinessException(403, "仅管理员或主管可批量处置逾期任务");
        }
        int success = 0, fail = 0;
        for (Long taskId : taskIds) {
            try {
                Task task = taskMapper.selectById(taskId);
                if (task == null || !"overdue".equals(task.getStatus())) {
                    fail++;
                    continue;
                }
                switch (action) {
                    case "complete" -> {
                        task.setStatus("completed");
                        task.setUpdatedAt(LocalDateTime.now());
                        taskMapper.updateById(task);
                        success++;
                    }
                    case "extend" -> {
                        Object deadlineObj = param != null ? param.get("newDeadline") : null;
                        if (deadlineObj != null) {
                            task.setDeadline(LocalDateTime.parse((String) deadlineObj));
                            task.setStatus("in_progress");
                            task.setOverdueMarked(0);
                            task.setUpdatedAt(LocalDateTime.now());
                            taskMapper.updateById(task);
                            success++;
                        } else {
                            fail++;
                        }
                    }
                    case "reassign" -> {
                        Object assigneeObj = param != null ? param.get("assigneeId") : null;
                        if (assigneeObj != null) {
                            Long assigneeId = ((Number) assigneeObj).longValue();
                            task.setAssigneeId(assigneeId);
                            task.setStatus("in_progress");
                            task.setOverdueMarked(0);
                            task.setUpdatedAt(LocalDateTime.now());
                            taskMapper.updateById(task);
                            success++;
                        } else {
                            fail++;
                        }
                    }
                    default -> fail++;
                }
            } catch (Exception e) {
                fail++;
            }
        }
        taskCacheService.evictAll();
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("fail", fail);
        return map;
    }

    /** Task → TaskListItemVO 转换，附加计算字段 */
    private TaskListItemVO convertToVO(Task task) {
        TaskListItemVO vo = new TaskListItemVO();
        vo.setId(task.getId());
        vo.setTitle(task.getTitle());
        vo.setDescription(task.getDescription());
        vo.setStatus(task.getStatus());
        vo.setPriority(task.getPriority());
        vo.setDeptId(task.getDeptId());
        vo.setTemplateId(task.getTemplateId());
        vo.setAssigneeMode(task.getAssigneeMode());
        vo.setGroupId(task.getGroupId());
        vo.setDeadline(task.getDeadline());
        vo.setRemark(task.getRemark());
        vo.setRejectRemark(task.getRejectRemark());
        vo.setRejectedAt(task.getRejectedAt());
        vo.setAcceptResult(task.getAcceptResult());
        vo.setAcceptRemark(task.getAcceptRemark());
        vo.setAcceptedAt(task.getAcceptedAt());
        vo.setCreatorId(task.getCreatorId());
        vo.setCreatorName(task.getCreatorName());
        vo.setAssigneeId(task.getAssigneeId());
        vo.setAssigneeName(task.getAssigneeName());
        vo.setCreatedAt(task.getCreatedAt());
        vo.setUpdatedAt(task.getUpdatedAt());

        // 多人协办模式：查询执行人ID列表
        if (task.getAssigneeMode() != null && task.getAssigneeMode() == TaskConstant.ASSIGNEE_MODE_MULTI) {
            try {
                LambdaQueryWrapper<TaskAssignee> aw = new LambdaQueryWrapper<TaskAssignee>()
                        .eq(TaskAssignee::getTaskId, task.getId())
                        .select(TaskAssignee::getUserId);
                List<Long> userIds = taskAssigneeMapper.selectList(aw).stream()
                        .map(TaskAssignee::getUserId)
                        .toList();
                vo.setMultiAssigneeIds(userIds);
            } catch (Exception e) {
                log.warn("[convertToVO] 查询多人协办执行人失败, taskId={}: {}", task.getId(), e.getMessage());
            }
        }

        // 计算剩余工期 / 逾期天数
        LocalDateTime now = LocalDateTime.now();
        if (task.getDeadline() != null && !"completed".equals(task.getStatus())) {
            long days = java.time.Duration.between(now, task.getDeadline()).toDays();
            vo.setRemainingDays(days);
            if (days < 0) {
                vo.setOverdueDays(-days);
            }
        }

        // 反馈次数
        LambdaQueryWrapper<ProgressFeedback> fbCount = new LambdaQueryWrapper<ProgressFeedback>()
                .eq(ProgressFeedback::getTaskId, task.getId());
        Long fbCountNum = progressFeedbackMapper.selectCount(fbCount);
        vo.setFeedbackCount(fbCountNum != null ? fbCountNum.intValue() : 0);

        return vo;
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
