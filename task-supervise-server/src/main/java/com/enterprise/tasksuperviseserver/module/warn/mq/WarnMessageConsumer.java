package com.enterprise.tasksuperviseserver.module.warn.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskAssignee;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskAssigneeMapper;
import com.enterprise.tasksuperviseserver.module.warn.entity.InAppMessage;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRecord;
import com.enterprise.tasksuperviseserver.module.warn.mapper.InAppMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警消息消费者
 * <p>
 * 收到预警消息后，为任务所有指派人创建站内消息（in_app_message）。
 * 仅在 spring.rabbitmq.listener.simple.auto-startup=true 时激活。
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.auto-startup", havingValue = "true")
public class WarnMessageConsumer {

    private final InAppMessageMapper inAppMessageMapper;
    private final TaskAssigneeMapper taskAssigneeMapper;

    @RabbitListener(queues = com.enterprise.tasksuperviseserver.config.RabbitMqConfig.QUEUE)
    public void handleWarnMessage(WarnRecord record) {
        try {
            log.info("收到预警消息: taskId={}, level={}", record.getTaskId(), record.getLevel());

            if (record.getTaskId() == null) {
                log.warn("预警消息缺少 taskId，跳过");
                return;
            }

            // 查询任务所有指派人
            List<TaskAssignee> assignees = taskAssigneeMapper.selectList(
                    new LambdaQueryWrapper<TaskAssignee>()
                            .eq(TaskAssignee::getTaskId, record.getTaskId()));

            if (assignees.isEmpty()) {
                log.warn("任务 {} 无指派人，跳过站内消息", record.getTaskId());
                return;
            }

            String title = switch (record.getLevel() != null ? record.getLevel() : 1) {
                case 3 -> "紧急预警";
                case 2 -> "重要预警";
                default -> "一般预警";
            };

            String content = record.getWarnContent() != null ? record.getWarnContent() : "您有新的任务预警，请及时处理";

            for (TaskAssignee assignee : assignees) {
                InAppMessage msg = new InAppMessage();
                msg.setUserId(assignee.getUserId());
                msg.setTitle(title);
                msg.setContent(content);
                msg.setLevel(record.getLevel() != null ? record.getLevel() : 1);
                msg.setReadStatus(0);
                msg.setCreatedAt(LocalDateTime.now());
                inAppMessageMapper.insert(msg);
            }

            log.info("预警消息处理完成: 为 {} 名指派人创建站内消息", assignees.size());

        } catch (Exception e) {
            log.error("预警消息处理失败: {}", e.getMessage(), e);
        }
    }
}
