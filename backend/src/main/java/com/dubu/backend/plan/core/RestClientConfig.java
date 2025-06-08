package com.dubu.backend.plan.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration("plan.restClientConfig")
@Slf4j
public class RestClientConfig {
    @Value("${api.local-url}")
    private String LOCAL_URL;

    @Bean("plan.restClient")
    public RestClient restClient(){
        log.info("Local URL: {}", LOCAL_URL);

        return RestClient.builder()
                .baseUrl(LOCAL_URL)
                .build();
    }
}
