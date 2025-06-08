package com.dubu.backend.member.application.dto;

import com.dubu.backend.member.domain.model.Member;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MemberDetailResult(
        Long memberId,
        String status,
        LocalDate createdAt
) {
    public static MemberDetailResult from(Member member){
        return MemberDetailResult.builder()
                .memberId(member.getId())
                .status(member.getStatus().name())
                .createdAt(member.getCreatedAt().toLocalDate())
                .build();
    }
}
