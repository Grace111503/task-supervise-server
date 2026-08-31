package com.enterprise.tasksuperviseserver.module.warn.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskAssigneeMapper;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import com.enterprise.tasksuperviseserver.module.warn.entity.InAppMessage;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRecord;
import com.enterprise.tasksuperviseserver.module.warn.mapper.InAppMessageMapper;
import com.enterprise.tasksuperviseserver.module.warn.websocket.MessageWebSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 预警消息消费者
 * <p>
 * 收到预警消息后，根据预警级别为不同角色创建站内消息：
 * <ul>
 *   <li>level=1（普通）：执行人</li>
 *   <li>level=2（重要）：执行人 + 创建人</li>
 *   <li>level=3（紧急）：执行人 + 创建人（每日重复提醒）</li>
 * </ul>
 * 同时通过 WebSocket 实时推送给在线用户。
 *
 * @author grq
 * @date 2026-08-27
 * @version v2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.auto-startup", havingValue = "true")
public class WarnMessageConsumer {

    private final InAppMessageMapper inAppMessageMapper;
    private final TaskAssigneeMapper taskAssigneeMapper;
    private final TaskMapper taskMapper;

    @RabbitListener(queues = com.enterprise.tasksuperviseserver.config.RabbitMqConfig.QUEUE)
    public void handleWarnMessage(WarnRecord record) {
        try {
            log.info("收到预警消息: taskId={}, level={}", record.getTaskId(), record.getLevel());

            if (record.getTaskId() == null) {
                log.warn("预警消息缺少 taskId，跳过");
                return;
            }

            Task task = taskMapper.selectById(record.getTaskId());
            int level = record.getLevel() != null ? record.getLevel() : 1;
            String title = switch (level) {
                case 3 -> "🔴 紧急预警";
                case 2 -> "🟡 重要预警";
                default -> "🔵 一般提醒";
            };
            String content = record.getWarnContent() != null ? record.getWarnContent() : "您有新的任务预警，请及时处理";

            // 收集需要通知的用户 ID（去重）
            Set<Long> notifyUserIds = new HashSet<>();

            // level >= 1：通知所有执行人
            List<TaskAssignee> assignees = taskAssigneeMapper.selectList(
                    new LambdaQueryWrapper<TaskAssignee>()
                            .eq(TaskAssignee::getTaskId, record.getTaskId()));
            for (TaskAssignee assignee : assignees) {
                notifyUserIds.add(assignee.getUserId());
            }

            // level >= 2：通知创建人
            if (level >= 2 && task != null && task.getCreatorId() != null) {
                notifyUserIds.add(task.getCreatorId());
            }

            if (notifyUserIds.isEmpty()) {
                log.warn("任务 {} 无通知目标，跳过", record.getTaskId());
                return;
            }

            // 批量创建站内消息
            for (Long userId : notifyUserIds) {
                InAppMessage msg = new InAppMessage();
                msg.setUserId(userId);
                msg.setTitle(title);
                msg.setContent(content);
                msg.setLevel(level);
                msg.setReadStatus(0);
                msg.setCreatedAt(LocalDateTime.now());
                inAppMessageMapper.insert(msg);

                // WebSocket 实时推送
                try {
                    MessageWebSocket.sendToUser(userId, "new_message",
                            String.format("{\"title\":\"%s\",\"content\":\"%s\",\"level\":%d}", title, content, level));
                } catch (Exception ignored) {
                }
            }

            log.info("预警消息处理完成: 为 {} 名用户创建站内消息 (level={})", notifyUserIds.size(), level);

        } catch (Exception e) {
            log.error("预警消息处理失败: {}", e.getMessage(), e);
        }
    }
}
