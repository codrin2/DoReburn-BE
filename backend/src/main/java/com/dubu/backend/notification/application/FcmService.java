package com.dubu.backend.notification.application;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.notification.domain.FcmToken;
import com.dubu.backend.notification.dto.FcmTokenDto;
import com.dubu.backend.notification.dto.PushMessageDto;
import com.dubu.backend.notification.infra.repository.FcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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

    public void sendMessage(PushMessageDto message) throws FirebaseMessagingException {
        List<FcmToken> fcmTokens = fcmTokenRepository.findByMemberId(message.memberId());

        for (FcmToken fcmToken : fcmTokens) {
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
        }
    }
}