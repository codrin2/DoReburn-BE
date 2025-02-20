package com.dubu.backend.member.infra.amqp;

import com.dubu.backend.member.application.MemberService;
import com.dubu.backend.member.config.MemberRabbitMQConfig;
import com.dubu.backend.member.dto.MemberStatusChangeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStatusEventConsumer {
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = MemberRabbitMQConfig.DLX_QUEUE_NAME)
    public void receive(String jsonMessage) {
        try {
            MemberStatusChangeDto message = objectMapper.readValue(jsonMessage, MemberStatusChangeDto.class);
            log.info("[멤버 상태 전환 이벤트 실행] message : {}", message);
            memberService.updateMemberStatusByPlanChange(message);
        } catch (Exception e) {
            log.error("메시지 변환 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}