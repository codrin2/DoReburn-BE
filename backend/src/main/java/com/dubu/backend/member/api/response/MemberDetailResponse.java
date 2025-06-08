package com.dubu.backend.member.api.response;

import com.dubu.backend.member.application.dto.MemberDetailResult;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MemberDetailResponse(
        Long memberId,
        String status,
        LocalDate createdAt
){
    public static MemberDetailResponse from(MemberDetailResult result){
        return MemberDetailResponse.builder()
                .memberId(result.memberId())
                .status(result.status())
                .createdAt(result.createdAt())
                .build();
    }
}
