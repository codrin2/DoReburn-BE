package com.dubu.backend.core.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQNotificationConfig {
    public static final String DELAY_QUEUE_NAME = "notification.delay.queue";
    public static final String DLX_QUEUE_NAME = "notification.dlx.queue";

    public static final String NOTIFICATION_EXCHANGE_NAME = "notification.exchange";

    public static final String DELAY_ROUTING_KEY = "notification.delay.key";
    public static final String DLX_ROUTING_KEY = "notification.dlx.key";

    @Value("${spring.rabbitmq.ttl.app-push}")
    private long pushDelayMs;

    @Bean
    public Queue notificationDelayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .withArgument("x-message-ttl", pushDelayMs)
                .build();
    }

    @Bean
    public Queue notificationDlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE_NAME).build();
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE_NAME);
    }

    @Bean
    public Binding notificationDelayQueueBinding(Queue notificationDelayQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationDelayQueue)
                .to(notificationExchange)
                .with(DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding notificationDlxQueueBinding(Queue notificationDlxQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationDlxQueue)
                .to(notificationExchange)
                .with(DLX_ROUTING_KEY);
    }
}