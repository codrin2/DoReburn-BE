package com.dubu.backend.member.infrastructure;

import com.dubu.backend.member.application.MemberCommandFacade;
import com.dubu.backend.core.config.RabbitMQMemberConfig;
import com.dubu.backend.member.application.event.MovementCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovementCompletedEventConsumer {
    private final MemberCommandFacade memberCommandFacade;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQMemberConfig.DLX_QUEUE_NAME)
    public void receive(String jsonMessage) {
        try {
            MovementCompletedEvent message = objectMapper.readValue(jsonMessage, MovementCompletedEvent.class);
            log.info("[멤버 상태 전환 이벤트 실행] message : {}", message);
            memberCommandFacade.updateMemberStatusByPlanChange(message);
        } catch (Exception e) {
            log.error("메시지 변환 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}