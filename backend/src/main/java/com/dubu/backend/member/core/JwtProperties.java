package com.dubu.backend.member.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpireTimeInHours,
        long refreshTokenExpireTimeInHours
) {
}