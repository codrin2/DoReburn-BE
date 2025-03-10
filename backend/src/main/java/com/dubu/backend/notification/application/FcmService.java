package com.dubu.backend.notification.application;

import com.dubu.backend.notification.api.dto.PushMessageDto;
import com.dubu.backend.notification.application.response.MemberResponse;
import com.dubu.backend.notification.domain.FcmToken;
import com.dubu.backend.notification.domain.repository.FcmTokenRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
    private final MemberApi memberApi;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveToken(String token, String deviceToken) {
        MemberResponse memberResponse = memberApi.getMemberByToken(token);

        boolean exists = fcmTokenRepository.findByMemberId(memberResponse.memberId())
                .stream()
                .anyMatch(fcmToken -> Objects.equals(fcmToken.getDeviceToken(), deviceToken));

        if (!exists) {
            fcmTokenRepository.save(FcmToken.createFcmToken(memberResponse.memberId(), deviceToken));
            log.info("[FCM Token 신규 저장] memberId={}", memberResponse.memberId());
        }
    }

    @Transactional(readOnly = true)
    public void send(PushMessageDto message) {
        List<FcmToken> fcmTokens = fcmTokenRepository.findByMemberId(message.memberId());

        for (FcmToken fcmToken : fcmTokens) {
            try {
                String response = FirebaseMessaging.getInstance().send(
                        Message.builder()
                                .setNotification(Notification.builder()
                                        .setTitle(message.title())
                                        .setBody(message.body())
                                        .build())
                                .setToken(fcmToken.getDeviceToken())
                                .build()
                );

                log.info("[FCM 전송 결과] memberId={}, deviceToken={}, response={}",
                        message.memberId(), fcmToken.getDeviceToken(), response);

            } catch (FirebaseMessagingException e) {
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    log.error("등록 해제된 토큰입니다. token={}", fcmToken.getDeviceToken());
                    fcmTokenRepository.delete(fcmToken);
                } else {
                    log.error("FCM 메시지 전송 실패: {}", e.getMessage());
                }
            }
        }
    }
}