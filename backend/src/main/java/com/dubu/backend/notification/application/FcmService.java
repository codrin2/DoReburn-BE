package com.dubu.backend.notification.application;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.notification.domain.FcmToken;
import com.dubu.backend.notification.dto.FcmTokenDto;
import com.dubu.backend.notification.dto.PushMessageDto;
import com.dubu.backend.notification.exception.DuplicateFcmTokenException;
import com.dubu.backend.notification.exception.NotFoundFcmTokenException;
import com.dubu.backend.notification.infra.repository.FcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        fcmTokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        existingToken -> {
                            if(Objects.equals(existingToken.getDeviceToken(), fcmTokenDto.deviceToken())){
                                throw new DuplicateFcmTokenException(memberId);
                            }
                            fcmTokenRepository.save(FcmToken.createFcmToken(member, fcmTokenDto.deviceToken()));
                            log.info("[FCM Token 신규 저장] memberId={}", memberId);
                        },
                        () -> {
                            fcmTokenRepository.save(FcmToken.createFcmToken(member, fcmTokenDto.deviceToken()));
                            log.info("[FCM Token 신규 저장] memberId={}", memberId);
                        }
                );
    }

    public void sendMessage(PushMessageDto message) throws FirebaseMessagingException {
        FcmToken currentFcmToken =  fcmTokenRepository.findByMemberId(message.memberId())
                .orElseThrow(() -> new NotFoundFcmTokenException(message.memberId()));

        String response = FirebaseMessaging.getInstance().send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(message.title())
                        .setBody(message.body())
                        .build())
                .setToken(currentFcmToken.getDeviceToken())
                .build());

        System.out.println("Sent message: " + response);
    }
}
