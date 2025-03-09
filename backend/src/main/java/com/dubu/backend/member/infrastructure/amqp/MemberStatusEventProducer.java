package com.dubu.backend.member.infrastructure.amqp;

import com.dubu.backend.member.core.MemberRabbitMQConfig;
import com.dubu.backend.member.dto.MemberStatusChangeDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStatusEventProducer {
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    public void send(MemberStatusChangeDto message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);

            amqpTemplate.convertAndSend(
                    MemberRabbitMQConfig.MEMBER_EXCHANGE_NAME,
                    MemberRabbitMQConfig.DELAY_ROUTING_KEY,
                    jsonMessage
            );
            log.info("[멤버 상태 변경 이벤트 생성] message : {}", message);
        } catch (JsonProcessingException e) {
            log.error("메시지 변환 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}