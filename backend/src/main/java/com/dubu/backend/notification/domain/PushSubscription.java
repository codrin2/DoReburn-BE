package com.dubu.backend.notification.domain;

import com.dubu.backend.core.domain.BaseTimeEntity;
import com.dubu.backend.notification.api.dto.PushSubscriptionDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "end_point", "p256dh", "auth"})
})
public class PushSubscription extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "end_point", nullable = false)
    private String endPoint;

    @Column(name = "p256dh", nullable = false)
    private String p256dh;

    @Column(name = "auth", nullable = false)
    private String auth;

    public static PushSubscription createSubscription(Long memberId, PushSubscriptionDto pushSubscriptionDto) {
        return PushSubscription.builder()
                .memberId(memberId)
                .endPoint(pushSubscriptionDto.endpoint())
                .p256dh(pushSubscriptionDto.keys().p256dh())
                .auth(pushSubscriptionDto.keys().auth())
                .build();
    }
}