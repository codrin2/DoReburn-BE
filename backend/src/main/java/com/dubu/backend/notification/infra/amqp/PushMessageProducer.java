package com.dubu.backend.notification.infra.amqp;

import com.dubu.backend.notification.config.RabbitMQConfig;
import com.dubu.backend.notification.dto.PushMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushMessageProducer {
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    public void sendDelayedPush(PushMessageDto message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);

            amqpTemplate.convertAndSend(
                    RabbitMQConfig.DELAY_EXCHANGE_NAME,
                    RabbitMQConfig.DELAY_ROUTING_KEY,
                    jsonMessage
            );
        } catch (JsonProcessingException e) {
            log.error("메시지 변환 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}