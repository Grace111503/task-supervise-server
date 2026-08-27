package com.enterprise.tasksuperviseserver.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 配置类
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "task.warn.exchange";
    public static final String QUEUE = "task.warn.queue";
    public static final String ROUTING_KEY = "task.warn.routing.key";

    @Bean
    public DirectExchange warnExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue warnQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 86400000);
        return new Queue(QUEUE, true, false, false, args);
    }

    @Bean
    public Binding warnBinding() {
        return BindingBuilder.bind(warnQueue())
                .to(warnExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
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
