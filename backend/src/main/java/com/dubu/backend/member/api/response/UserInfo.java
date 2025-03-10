package com.dubu.backend.member.api.response;

public record UserInfo(
        String oauthProviderId,
        String email
) {
}