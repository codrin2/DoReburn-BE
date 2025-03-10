package com.dubu.backend.notification.domain;

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

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String deviceToken;

    public static FcmToken createFcmToken(Long memberId, String deviceToken) {
        return FcmToken.builder()
                .memberId(memberId)
                .deviceToken(deviceToken)
                .build();
    }
}