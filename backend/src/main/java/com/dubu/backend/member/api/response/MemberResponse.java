package com.dubu.backend.member.api.response;

import com.dubu.backend.member.domain.Member;

public record MemberResponse(
        Long memberId,
        String status,
        String recentRoute
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getStatus().toString(),
                member.getRecentRoute()
        );
    }
}