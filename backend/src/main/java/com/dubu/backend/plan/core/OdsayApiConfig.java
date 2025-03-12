package com.dubu.backend.plan.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "odsay.client")
public record OdsayApiConfig(
    String apiKey
) {
}