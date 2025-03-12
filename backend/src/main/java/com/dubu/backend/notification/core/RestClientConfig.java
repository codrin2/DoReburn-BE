package com.dubu.backend.notification.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean(name = "notification.memberRestClient")
    public RestClient memberRestClient(
            @Value("${api.base-url}") String baseUrl
    ) {
        return RestClient.create(baseUrl);
    }
}