package com.dubu.backend.notification.application;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.application.event.MovementCompletedEvent;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.notification.infra.MovementCompletedEventProducer;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.notification.core.VapidKeyConfig;
import com.dubu.backend.notification.domain.PushSubscription;
import com.dubu.backend.notification.dto.PushMessageDto;
import com.dubu.backend.notification.dto.PushSubscriptionDto;
import com.dubu.backend.notification.exception.DuplicateSubscriptionException;
import com.dubu.backend.notification.exception.UnavailablePushServiceException;
import com.dubu.backend.notification.infra.amqp.PushMessageEventProducer;
import com.dubu.backend.notification.infra.repository.PushSubscriptionRepository;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final VapidKeyConfig vapidKeyConfig;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final PushSubscriptionRepository subscriptionRepository;
    private final PushMessageEventProducer pushMessageEventProducer;
    private final MovementCompletedEventProducer movementCompletedEventProducer;
    private final ObjectMapper objectMapper;

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${notification.url}")
    private String planUrl;

    @Transactional
    public void saveSubscription(Long memberId, PushSubscriptionDto subscriptionDto) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        PushSubscription pushSubscription = PushSubscription.createSubscription(currentMember, subscriptionDto);

        try {
            subscriptionRepository.save(pushSubscription);
            log.info("[구독 저장] memberId={}, endpoint={}", memberId, subscriptionDto.endpoint());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateSubscriptionException();
        }
    }

    public void sendPushNotification(PushMessageDto message) {
        Plan currentPlan = planRepository.findById(message.planId())
                .orElseThrow(() -> new PlanNotFoundException(message.planId()));

        if (currentPlan.isCompleted()) {
            return;
        }

        List<PushSubscription> subscriptions = subscriptionRepository.findByMemberId((message.memberId()));
        PushService pushService;

        try {
            pushService = new PushService(vapidKeyConfig.publicKey(), vapidKeyConfig.privateKey(), adminEmail);
        } catch (GeneralSecurityException e) {
            throw new UnavailablePushServiceException();
        }

        for (PushSubscription sub : subscriptions) {
            try {
                Map<String, Object> payloadMap = Map.of(
                        "notification", Map.of(
                                "title", message.title(),
                                "body", message.body()
                        ),
                        "data", Map.of(
                                "url", planUrl
                        )
                );

                String payload = objectMapper.writeValueAsString(payloadMap);

                Notification notification = new Notification(
                        sub.getEndPoint(),
                        sub.getP256dh(),
                        sub.getAuth(),
                        payload.getBytes(StandardCharsets.UTF_8)
                );

                pushService.send(notification);
                log.info("[푸시 알림 전송] memberId={} ", message.memberId());
            } catch (Exception e) {
                throw new UnavailablePushServiceException();
            }
        }
    }

    public void sendPushAndMemberStatusChange(Long memberId, Plan plan) {
        // 푸시 메시지 이벤트 발행
        PushMessageDto pushMessageDto = new PushMessageDto(
                memberId,
                plan.getId(),
                "잘 도착하셨나요? 30분 뒤면 오늘 한 일을 체크할 수 없어요😭",
                "얼른 접속해서 오늘 한 일을 체크하고 피드백을 기록해 보세요~"
        );
        pushMessageEventProducer.sendDelayedPush(pushMessageDto);

        // 상태 변경 이벤트 발행
        MovementCompletedEvent movementCompletedEvent = new MovementCompletedEvent(
                memberId,
                plan.getId()
        );
        movementCompletedEventProducer.send(movementCompletedEvent);
    }
}