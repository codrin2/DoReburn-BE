package com.dubu.backend.member.presentation.response;

public record Token(
        String accessToken,
        String refreshToken
) {
}