package com.dubu.backend.notification.infra.amqp;

import com.dubu.backend.notification.application.FcmService;
import com.dubu.backend.notification.application.NotificationService;
import com.dubu.backend.notification.core.NotificationRabbitMQConfig;
import com.dubu.backend.notification.dto.PushMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushMessageEventConsumer {
    private final NotificationService notificationService;
    private final FcmService fcmService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = NotificationRabbitMQConfig.DLX_QUEUE_NAME)
    public void receivePushMessage(String jsonMessage) {
        try {
            PushMessageDto message = objectMapper.readValue(jsonMessage, PushMessageDto.class);
            log.info("[푸시 알림 이벤트 소모] message : {}", message);
            notificationService.sendPushNotification(message);
            fcmService.sendMessage(message);
        } catch (Exception e) {
            log.error("메시지 변환 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}