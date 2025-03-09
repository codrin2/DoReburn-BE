package com.dubu.backend.member.infrastructure.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "naver.client")
public record NaverApiProperties(
        String clientId,
        String clientSecret
) {
}