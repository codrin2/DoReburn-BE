package com.dubu.backend.member.domain.model;

import com.dubu.backend.core.domain.BaseTimeEntity;
import com.dubu.backend.member.domain.enums.OauthProvider;
import com.dubu.backend.member.domain.enums.Role;
import com.dubu.backend.member.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TempMember extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 50)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @Column(nullable = false)
    private String oauthProviderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'USER'")
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ONBOARDING'")
    private Status status;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point location;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updateLocation(Point location){
        this.location = location;
    }

    public boolean hasLocation(){
        return this.location != null;
    }
}
