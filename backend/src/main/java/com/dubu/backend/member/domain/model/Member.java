package com.dubu.backend.member.domain.model;

import com.dubu.backend.core.domain.BaseTimeEntity;
import com.dubu.backend.core.util.DateTimeUtils;
import com.dubu.backend.member.core.AggregateRoot;
import com.dubu.backend.member.domain.enums.MemberStatus;
import com.dubu.backend.member.domain.enums.OauthProvider;
import com.dubu.backend.member.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@AggregateRoot
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 50)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Embedded
    private OauthInfo oauthInfo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private String recentRoute;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public static Member of(String email, OauthProvider oauthProvider, String oauthProviderId) {
        return Member.builder()
                .email(email)
                .oauthInfo(OauthInfo.of(oauthProvider, oauthProviderId))
                .role(Role.USER)
                .status(MemberStatus.ONBOARDING)
                .build();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateStatus(MemberStatus status) {
        this.status = status;
    }

    public boolean isOnboarding() {
        return this.status == MemberStatus.ONBOARDING;
    }

    public void deactivate() {
        this.email = "deactivated-" + this.id;
        this.oauthInfo = OauthInfo.of(this.getOauthInfo().getOauthProvider(), "0");
        this.deletedAt = DateTimeUtils.nowSeoulInstant();
    }
}
