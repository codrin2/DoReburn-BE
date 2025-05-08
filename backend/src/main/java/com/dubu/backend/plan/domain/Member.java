package com.dubu.backend.plan.domain;

import com.dubu.backend.core.domain.BaseTimeEntity;
import com.dubu.backend.plan.domain.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "PlanMember")
@Table(name = "member")
@Getter
public class Member extends BaseTimeEntity {
    @Id
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public void updateStatus(MemberStatus status){
        this.status = status;
    }
}
