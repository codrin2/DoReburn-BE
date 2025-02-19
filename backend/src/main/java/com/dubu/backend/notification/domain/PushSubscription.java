package com.dubu.backend.notification.domain;

import com.dubu.backend.global.domain.BaseTimeEntity;
import com.dubu.backend.member.domain.Member;
import com.dubu.backend.notification.dto.PushSubscriptionDto;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String endPoint;

    @Column(nullable = false)
    private String p256dh;

    @Column(nullable = false)
    private String auth;

    public static PushSubscription createSubscription(Member member, PushSubscriptionDto pushSubscriptionDto) {
        return PushSubscription.builder()
                .member(member)
                .endPoint(pushSubscriptionDto.endpoint())
                .p256dh(pushSubscriptionDto.keys().p256dh())
                .auth(pushSubscriptionDto.keys().auth())
                .build();
    }
}