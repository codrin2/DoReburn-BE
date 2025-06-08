package com.dubu.backend.member.api.response;

import lombok.Builder;

@Builder
public record MemberNicknameResponse(
        String nickname
) {
    public static MemberNicknameResponse of(String nickname){
        return MemberNicknameResponse.builder()
                .nickname(nickname)
                .build();
    }
}
