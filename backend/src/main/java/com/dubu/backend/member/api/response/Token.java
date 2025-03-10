package com.dubu.backend.member.api.response;

public record Token(
        String accessToken,
        String refreshToken
) {
}