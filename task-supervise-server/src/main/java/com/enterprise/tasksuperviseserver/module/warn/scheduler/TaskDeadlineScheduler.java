package com.enterprise.tasksuperviseserver.module.warn.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRecord;
import com.enterprise.tasksuperviseserver.module.warn.service.WarnRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
 * @version v1.0.0
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TaskDeadlineScheduler {

    private final TaskMapper taskMapper;

    @Autowired(required = false)
    private WarnRecordService warnRecordService;

    /**
     * 每天 09:00 扫描任务到期情况
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void scanDeadlines() {
        log.info("开始扫描任务到期预警...");
        int totalWarned = 0;

        try {
            // 查询所有未完成、未删除的任务
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
        } catch (Exception e) {
            log.error("扫描任务到期预警异常: {}", e.getMessage(), e);
        }

        log.info("任务到期预警扫描完成，共触发 {} 条预警", totalWarned);
    }

    /**
     * 检查单个任务并触发预警
     *
     * @return 触发的预警数量
     */
    private int checkAndWarn(Task task, LocalDate today, LocalDateTime now) {
        LocalDateTime deadline = task.getDeadline();
        LocalDate deadlineDate = deadline.toLocalDate();
        long daysLeft = Duration.between(now, deadline).toDays();

        int warned = 0;

        if (daysLeft <= 0) {
            // ===== 已逾期：level=3 紧急 =====
            // 自动标记逾期状态
            if (!"overdue".equals(task.getStatus()) && task.getOverdueMarked() == null || task.getOverdueMarked() != 1) {
                task.setStatus("overdue");
                task.setOverdueMarked(1);
                task.setUpdatedAt(now);
                taskMapper.updateById(task);
                log.info("任务自动标记逾期: taskId={}, deadline={}", task.getId(), deadline);
            }
            // 防重复：今天是否已发过 level=3 预警
            if (!hasWarnedToday(task.getId(), 3, today)) {
                createWarnRecord(task, 3,
                        String.format("任务「%s」已逾期 %d 天，请立即处理！", task.getTitle(), Math.abs(daysLeft)));
                warned++;
            }
        } else if (daysLeft <= 3) {
            // ===== 到期前 3 天：level=2 重要 =====
            if (!hasWarnedToday(task.getId(), 2, today)) {
                createWarnRecord(task, 2,
                        String.format("任务「%s」将在 %d 天后到期，请尽快完成！", task.getTitle(), daysLeft));
                warned++;
            }
        } else if (daysLeft <= 7) {
            // ===== 到期前 7 天：level=1 普通 =====
            if (!hasWarnedToday(task.getId(), 1, today)) {
                createWarnRecord(task, 1,
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
        if (warnRecordService == null) return false;
        // 通过 Mapper 直接查
        LambdaQueryWrapper<WarnRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarnRecord::getTaskId, taskId)
                .eq(WarnRecord::getLevel, level)
                .ge(WarnRecord::getPushTime, today.atStartOfDay())
                .le(WarnRecord::getPushTime, today.atTime(LocalTime.MAX));
        return warnRecordService.page(1, 1, taskId, level).getRecords().stream()
                .anyMatch(r -> r.getPushTime() != null
                        && r.getPushTime().toLocalDate().equals(today)
                        && level == (r.getLevel() != null ? r.getLevel() : 0));
    }

    /**
     * 创建预警记录（通过 WarnRecordService 触发 MQ 推送）
     */
    private void createWarnRecord(Task task, int level, String content) {
        if (warnRecordService == null) {
            log.warn("WarnRecordService 不可用，跳过预警: taskId={}", task.getId());
            return;
        }
        WarnRecord record = new WarnRecord();
        record.setTaskId(task.getId());
        record.setLevel(level);
        record.setWarnContent(content);
        warnRecordService.create(record);
        log.info("创建预警记录: taskId={}, level={}, content={}", task.getId(), level, content);
    }
}