package com.dubu.backend.notification.infra.repository;

import com.dubu.backend.notification.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    List<FcmToken> findByMemberId(Long memberId);
}