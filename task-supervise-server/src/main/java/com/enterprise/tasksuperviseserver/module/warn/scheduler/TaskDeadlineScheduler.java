package com.enterprise.tasksuperviseserver.module.warn.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysUserMapper;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskAssigneeMapper;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRecord;
import com.enterprise.tasksuperviseserver.module.warn.mapper.WarnRecordMapper;
import com.enterprise.tasksuperviseserver.module.warn.service.NotificationService;
import com.enterprise.tasksuperviseserver.module.warn.service.WarnRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务到期预警定时扫描器
 * <p>
 * 每天早 9 点扫描所有未完成任务，根据剩余天数触发三级预警：
 * <ul>
 *   <li>level=1（普通）：到期前 7 天，通知执行人</li>
 *   <li>level=2（重要）：到期前 3 天，通知执行人 + 创建人</li>
 *   <li>level=3（紧急）：已逾期，通知执行人 + 创建人 + 主管/管理员，自动标记逾期</li>
 * </ul>
 *
 * @author grq
 * @date 2026-08-28
 * @version v1.1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDeadlineScheduler {

    private final TaskMapper taskMapper;
    private final TaskAssigneeMapper taskAssigneeMapper;
    private final SysUserMapper sysUserMapper;
    private final WarnRecordMapper warnRecordMapper;

    @Autowired(required = false)
    private WarnRecordService warnRecordService;

    @Autowired(required = false)
    private NotificationService notificationService;

    /**
     * 服务器启动完成后立即扫描一次，避免错过当天预警
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("服务器启动，立即执行一次到期预警扫描...");
        scanDeadlines();
    }

    /**
     * 每天 09:00 扫描任务到期情况
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void scanDeadlines() {
        log.info("开始扫描任务到期预警...");
        int totalWarned = 0;

        try {
            // 查询所有未完成、未删除、有截止时间的任务
            LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
            wrapper.ne(Task::getStatus, "completed")
                    .ne(Task::getStatus, "overdue")
                    .eq(Task::getDeleted, 0)
                    .isNotNull(Task::getDeadline);
            List<Task> tasks = taskMapper.selectList(wrapper);

            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();

            for (Task task : tasks) {
                try {
                    int warned = checkAndWarn(task, today, now);
                    totalWarned += warned;
                } catch (Exception e) {
                    log.warn("检查任务 {} 预警失败: {}", task.getId(), e.getMessage());
                }
            }

            // 额外扫描已标记逾期的任务，发送每日逾期提醒
            scanOverdueTasks(today, now);
        } catch (Exception e) {
            log.error("扫描任务到期预警异常: {}", e.getMessage(), e);
        }

        log.info("任务到期预警扫描完成，共触发 {} 条预警", totalWarned);
    }

    /**
     * 扫描已逾期任务，每日发送紧急提醒
     */
    private void scanOverdueTasks(LocalDate today, LocalDateTime now) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getStatus, "overdue")
                .eq(Task::getDeleted, 0)
                .isNotNull(Task::getDeadline);
        List<Task> overdueTasks = taskMapper.selectList(wrapper);

        for (Task task : overdueTasks) {
            try {
                if (!hasWarnedToday(task.getId(), 3, today)) {
                    long daysOverdue = Duration.between(task.getDeadline(), now).toDays();
                    String content = String.format("任务「%s」已逾期 %d 天，请立即处理！",
                            task.getTitle(), Math.abs(daysOverdue));
                    createWarnRecordAndNotify(task, 3, content);
                    log.info("逾期每日提醒: taskId={}, 逾期{}天", task.getId(), Math.abs(daysOverdue));
                }
            } catch (Exception e) {
                log.warn("逾期任务 {} 提醒失败: {}", task.getId(), e.getMessage());
            }
        }
    }

    /**
     * 检查单个任务并触发预警
     *
     * @return 触发的预警数量
     */
    private int checkAndWarn(Task task, LocalDate today, LocalDateTime now) {
        LocalDateTime deadline = task.getDeadline();
        long daysLeft = Duration.between(now, deadline).toDays();

        int warned = 0;

        if (daysLeft <= 0) {
            // ===== 已逾期：level=3 紧急 =====
            // 自动标记逾期状态
            if (!"overdue".equals(task.getStatus()) && (task.getOverdueMarked() == null || task.getOverdueMarked() != 1)) {
                task.setStatus("overdue");
                task.setOverdueMarked(1);
                task.setUpdatedAt(now);
                taskMapper.updateById(task);
                log.info("任务自动标记逾期: taskId={}, deadline={}", task.getId(), deadline);
            }
            // 防重复：今天是否已发过 level=3 预警
            if (!hasWarnedToday(task.getId(), 3, today)) {
                createWarnRecordAndNotify(task, 3,
                        String.format("任务「%s」已逾期 %d 天，请立即处理！", task.getTitle(), Math.abs(daysLeft)));
                warned++;
            }
        } else if (daysLeft <= 3) {
            // ===== 到期前 3 天：level=2 重要 =====
            if (!hasWarnedToday(task.getId(), 2, today)) {
                createWarnRecordAndNotify(task, 2,
                        String.format("任务「%s」将在 %d 天后到期，请尽快完成！", task.getTitle(), daysLeft));
                warned++;
            }
        } else if (daysLeft <= 7) {
            // ===== 到期前 7 天：level=1 普通 =====
            if (!hasWarnedToday(task.getId(), 1, today)) {
                createWarnRecordAndNotify(task, 1,
                        String.format("任务「%s」将在 %d 天后到期，请注意安排时间。", task.getTitle(), daysLeft));
                warned++;
            }
        }

        return warned;
    }

    /**
     * 检查今天是否已发过该级别的预警（防重复）
     */
    private boolean hasWarnedToday(Long taskId, int level, LocalDate today) {
        LambdaQueryWrapper<WarnRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarnRecord::getTaskId, taskId)
                .eq(WarnRecord::getLevel, level)
                .ge(WarnRecord::getPushTime, today.atStartOfDay())
                .le(WarnRecord::getPushTime, today.atTime(LocalTime.MAX));
        Long count = warnRecordMapper.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 创建预警记录并直接通知相关人员（绕过 RabbitMQ）
     */
    private void createWarnRecordAndNotify(Task task, int level, String content) {
        // 1. 创建预警记录（保留审计轨迹）
        if (warnRecordService != null) {
            WarnRecord record = new WarnRecord();
            record.setTaskId(task.getId());
            record.setLevel(level);
            record.setWarnContent(content);
            warnRecordService.create(record);
        }

        // 2. 确定通知目标用户
        List<Long> targetUserIds = getNotifyTargets(task, level);

        // 3. 为每个目标用户发送站内消息 + WebSocket 推送
        if (notificationService != null) {
            for (Long userId : targetUserIds) {
                try {
                    notificationService.sendNotification(
                            userId,
                            getWarnTitle(level),
                            content,
                            level,
                            "WARN",
                            task.getId()
                    );
                } catch (Exception e) {
                    log.debug("通知用户 {} 失败: {}", userId, e.getMessage());
                }
            }
            log.info("预警通知已发送: taskId={}, level={}, 目标用户数={}", task.getId(), level, targetUserIds.size());
        } else {
            log.warn("NotificationService 不可用，无法推送预警通知: taskId={}", task.getId());
        }
    }

    /**
     * 根据预警级别确定通知目标用户
     * <ul>
     *   <li>level=1：执行人</li>
     *   <li>level=2：执行人 + 创建人</li>
     *   <li>level=3：执行人 + 创建人 + 管理员/主管</li>
     * </ul>
     */
    private List<Long> getNotifyTargets(Task task, int level) {
        List<Long> targets = new ArrayList<>();

        // 查询任务执行人
        LambdaQueryWrapper<TaskAssignee> assigneeWrapper = new LambdaQueryWrapper<>();
        assigneeWrapper.eq(TaskAssignee::getTaskId, task.getId());
        List<Long> assigneeIds = taskAssigneeMapper.selectList(assigneeWrapper)
                .stream()
                .map(TaskAssignee::getUserId)
                .distinct()
                .collect(Collectors.toList());
        targets.addAll(assigneeIds);

        // level >= 2：加入创建人
        if (level >= 2 && task.getCreatorId() != null && !targets.contains(task.getCreatorId())) {
            targets.add(task.getCreatorId());
        }

        // level >= 3：加入管理员和主管
        if (level >= 3) {
            LambdaQueryWrapper<SysUser> adminWrapper = new LambdaQueryWrapper<>();
            adminWrapper.in(SysUser::getRoleCode, "admin", "manager");
            List<Long> adminIds = sysUserMapper.selectList(adminWrapper)
                    .stream()
                    .map(SysUser::getUserId)
                    .filter(uid -> !targets.contains(uid))
                    .collect(Collectors.toList());
            targets.addAll(adminIds);
        }

        return targets;
    }

    /**
     * 根据预警级别返回消息标题
     */
    private String getWarnTitle(int level) {
        return switch (level) {
            case 1 -> "任务到期提醒";
            case 2 -> "任务即将到期";
            case 3 -> "任务逾期警告";
            default -> "任务预警";
        };
    }
}
