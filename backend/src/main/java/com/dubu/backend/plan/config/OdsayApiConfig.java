package com.dubu.backend.plan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "odsay.client")
public record OdsayApiConfig(
    String apiKey
) {
}