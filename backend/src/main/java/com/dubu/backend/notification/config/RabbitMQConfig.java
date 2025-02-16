package com.dubu.backend.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String DELAY_QUEUE_NAME = "plan.delay.queue";
    public static final String DLX_QUEUE_NAME = "plan.dlx.queue";

    public static final String DELAY_EXCHANGE_NAME = "plan.delay.exchange";
    public static final String DLX_EXCHANGE_NAME = "plan.dlx.exchange";

    public static final String DELAY_ROUTING_KEY = "plan.delay.key";
    public static final String DLX_ROUTING_KEY = "plan.dlx.key";

    @Value("${app.push.delay-ms}")
    private long pushDelayMs;

    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .withArgument("x-message-ttl", pushDelayMs)
                .build();
    }

    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE_NAME).build();
    }

    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE_NAME);
    }

    @Bean
    public Binding delayQueueBinding(Queue delayQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayQueue)
                .to(delayExchange)
                .with(DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding dlxQueueBinding(Queue dlxQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue)
                .to(dlxExchange)
                .with(DLX_ROUTING_KEY);
    }
}