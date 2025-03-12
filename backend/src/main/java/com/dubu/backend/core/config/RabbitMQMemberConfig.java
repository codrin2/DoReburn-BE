package com.dubu.backend.core.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQMemberConfig {
    public static final String DELAY_QUEUE_NAME = "member.delay.queue";
    public static final String DLX_QUEUE_NAME = "member.dlx.queue";

    public static final String MEMBER_EXCHANGE_NAME = "member.exchange";

    public static final String DELAY_ROUTING_KEY = "member.delay.key";
    public static final String DLX_ROUTING_KEY = "member.dlx.key";

    @Value("${spring.rabbitmq.ttl.member-status}")
    private long pushDelayMs;

    @Bean
    public Queue memberDelayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", MEMBER_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .withArgument("x-message-ttl", pushDelayMs)
                .build();
    }

    @Bean
    public Queue memberDlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE_NAME).build();
    }

    @Bean
    public DirectExchange memberExchange() {
        return new DirectExchange(MEMBER_EXCHANGE_NAME);
    }

    @Bean
    public Binding memberDelayQueueBinding(Queue memberDelayQueue, DirectExchange memberExchange) {
        return BindingBuilder.bind(memberDelayQueue)
                .to(memberExchange)
                .with(DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding memberDlxQueueBinding(Queue memberDlxQueue, DirectExchange memberExchange) {
        return BindingBuilder.bind(memberDlxQueue)
                .to(memberExchange)
                .with(DLX_ROUTING_KEY);
    }
}