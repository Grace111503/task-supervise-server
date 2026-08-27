package com.enterprise.tasksuperviseserver.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 配置类
 * <p>
 * 职责：
 * <ol>
 *   <li>声明 Exchange / Queue / Binding（始终创建，确保 broker 上存在）</li>
 *   <li>声明 MessageConverter（始终创建）</li>
 *   <li>配置 ListenerContainerFactory（仅在 auto-startup=true 时创建）</li>
 * </ol>
 * 监听器的实际启动由 spring.rabbitmq.listener.simple.auto-startup 控制。
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "task.warn.exchange";
    public static final String QUEUE = "task.warn.queue";
    public static final String ROUTING_KEY = "task.warn.routing.key";

    /**
     * 声明直连交换机（持久化，不自动删除）
     */
    @Bean
    public DirectExchange warnExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    /**
     * 声明队列（持久化 + 消息 TTL 1 天，避免消息长期堆积）
     */
    @Bean
    public Queue warnQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 86400000);
        return new Queue(QUEUE, true, false, false, args);
    }

    /**
     * 绑定：queue → exchange with routing key
     */
    @Bean
    public Binding warnBinding() {
        return BindingBuilder.bind(warnQueue())
                .to(warnExchange())
                .with(ROUTING_KEY);
    }

    /**
     * 消息转换：JSON 序列化（始终创建，RabbitTemplate 需要）
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 监听容器工厂（仅在 auto-startup=true 时创建）
     * 与 Exchange/Queue/Binding 解耦：队列声明始终生效，监听器容器按需启停。
     */
    @Bean
    @ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.auto-startup", havingValue = "true")
    public SimpleRabbitListenerContainerFactory listenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(1);
        return factory;
    }
}
