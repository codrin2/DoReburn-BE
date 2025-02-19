package com.dubu.backend.notification.application;


import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.notification.config.VapidKeyConfig;
import com.dubu.backend.notification.domain.PushSubscription;
import com.dubu.backend.notification.dto.PushMessageDto;
import com.dubu.backend.notification.dto.PushSubscriptionDto;
import com.dubu.backend.notification.exception.DuplicateSubscriptionException;
import com.dubu.backend.notification.exception.UnavailablePushServiceException;
import com.dubu.backend.notification.infra.repository.PushSubscriptionRepository;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.infra.repository.PlanRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final VapidKeyConfig vapidKeyConfig;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final PushSubscriptionRepository subscriptionRepository;

    @Value("${admin.email}")
    private String adminEmail;

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
                String payload = """
                {
                    "title": "%s",
                    "body": "%s",
                    "url": "https://do-reburn.site/plan"
                }
                """.formatted(message.title(), message.body());

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
}