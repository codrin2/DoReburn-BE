package com.dubu.backend.member.presentation.response;

public record UserInfo(
        String oauthProviderId,
        String email
) {
}