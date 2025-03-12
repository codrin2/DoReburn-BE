package com.dubu.backend.notification.domain.repository;

import com.dubu.backend.notification.domain.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findByMemberId(Long memberId);
}