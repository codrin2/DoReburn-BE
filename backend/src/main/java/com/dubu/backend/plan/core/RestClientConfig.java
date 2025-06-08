package com.dubu.backend.plan.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration("plan.restClientConfig")
public class RestClientConfig {
    @Value("${api.local-url}")
    private String LOCAL_URL;

    @Value("${server.servlet.contextPath}")
    private String CONTEXT_PATH;

    @Bean("plan.restClient")
    public RestClient restClient(){
        return RestClient.builder()
                .baseUrl(LOCAL_URL + CONTEXT_PATH)
                .build();
    }
}
