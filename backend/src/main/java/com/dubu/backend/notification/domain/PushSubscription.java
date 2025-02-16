package com.dubu.backend.notification.domain;

import com.dubu.backend.global.domain.BaseTimeEntity;
import com.dubu.backend.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
}