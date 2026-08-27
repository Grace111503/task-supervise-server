package com.enterprise.tasksuperviseserver.module.warn.mq;

import com.enterprise.tasksuperviseserver.config.RabbitMqConfig;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 预警消息生产者
 * <p>
 * 将预警记录通过 RabbitMQ 异步推送到消息队列，
 * 消费者收到后写入 in_app_message 表并（可选）通过 WebSocket 推送。
 * 仅在 RabbitMQ 启用（auto-startup=true）时激活。
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.auto-startup", havingValue = "true")
public class WarnMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送预警消息到队列
     *
     * @param record 预警记录
     */
    public void send(WarnRecord record) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.EXCHANGE,
                    RabbitMqConfig.ROUTING_KEY,
                    record);
            log.info("预警消息已发送到队列: taskId={}, level={}", record.getTaskId(), record.getLevel());
        } catch (Exception e) {
            log.error("预警消息发送失败: {}", e.getMessage());
        }
    }
}
