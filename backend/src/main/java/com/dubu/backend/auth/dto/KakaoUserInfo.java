package com.dubu.backend.auth.dto;

public record KakaoUserInfo(
        String oauthProviderId,
        String email
) {
}