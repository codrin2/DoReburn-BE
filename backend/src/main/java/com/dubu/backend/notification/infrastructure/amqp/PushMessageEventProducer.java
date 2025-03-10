package com.dubu.backend.notification.infrastructure.amqp;

import com.dubu.backend.notification.core.NotificationRabbitMQConfig;
import com.dubu.backend.notification.api.dto.PushMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushMessageEventProducer {
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    public void sendDelayedPush(PushMessageDto message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);

            amqpTemplate.convertAndSend(
                    NotificationRabbitMQConfig.NOTIFICATION_EXCHANGE_NAME,
                    NotificationRabbitMQConfig.DELAY_ROUTING_KEY,
                    jsonMessage
            );
            log.info("[푸시 알림 이벤트 발급] message : {}", message);
        } catch (JsonProcessingException e) {
            log.error("메시지 변환 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}