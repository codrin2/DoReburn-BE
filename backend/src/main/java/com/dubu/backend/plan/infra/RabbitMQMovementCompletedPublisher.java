package com.dubu.backend.plan.infra;

import com.dubu.backend.core.config.RabbitMQMemberConfig;
import com.dubu.backend.member.application.event.MovementCompletedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQMovementCompletedPublisher {
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    public void send(MovementCompletedEvent message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);

            amqpTemplate.convertAndSend(
                    RabbitMQMemberConfig.MEMBER_EXCHANGE_NAME,
                    RabbitMQMemberConfig.DELAY_ROUTING_KEY,
                    jsonMessage
            );
            log.info("[멤버 상태 변경 이벤트 생성] message : {}", message);
        } catch (JsonProcessingException e) {
            log.error("메시지 변환 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}