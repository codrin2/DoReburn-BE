package com.dubu.backend.member.infrastructure.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.client")
public record KakaoApiProperties(
        String authorizationKey
) {
}