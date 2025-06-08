package com.dubu.backend.plan.domain;

import com.dubu.backend.plan.domain.enums.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class Member {
    private Long id;
    private MemberStatus status;
    private LocalDate createdAt;

    public static Member of(Long id, String status, LocalDate createdAt){
        return Member.builder()
                .id(id)
                .status(MemberStatus.fromString(status))
                .createdAt(createdAt)
                .build();
    }
}
