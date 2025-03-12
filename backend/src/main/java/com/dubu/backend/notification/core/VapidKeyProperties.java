package com.dubu.backend.notification.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vapid")
public record VapidKeyProperties(
        String publicKey,
        String privateKey
) {
}