package com.dubu.backend.notification.application;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infrastructure.repository.MemberRepository;
import com.dubu.backend.notification.domain.FcmToken;
import com.dubu.backend.notification.dto.FcmTokenDto;
import com.dubu.backend.notification.dto.PushMessageDto;
import com.dubu.backend.notification.infra.repository.FcmTokenRepository;
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
    private final MemberRepository memberRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveToken(Long memberId, FcmTokenDto fcmTokenDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        boolean exists = fcmTokenRepository.findByMemberId(memberId)
                .stream()
                .anyMatch(token -> Objects.equals(token.getDeviceToken(), fcmTokenDto.deviceToken()));

        if (!exists) {
            fcmTokenRepository.save(FcmToken.createFcmToken(member, fcmTokenDto.deviceToken()));
            log.info("[FCM Token 신규 저장] memberId={}", memberId);
        }
    }

    public void sendMessage(PushMessageDto message) {
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