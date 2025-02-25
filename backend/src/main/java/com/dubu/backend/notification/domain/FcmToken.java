package com.dubu.backend.notification.domain;

import com.dubu.backend.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "deviceToken"})
})
public class FcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String deviceToken;

    public static FcmToken createFcmToken(Member member, String deviceToken) {
        return FcmToken.builder()
                .member(member)
                .deviceToken(deviceToken)
                .build();
    }
}