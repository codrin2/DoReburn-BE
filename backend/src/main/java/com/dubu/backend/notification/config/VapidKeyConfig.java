package com.dubu.backend.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vapid")
public record VapidKeyConfig(
        String publicKey,
        String privateKey
) {
}