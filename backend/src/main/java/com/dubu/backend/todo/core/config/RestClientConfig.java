package com.dubu.backend.todo.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration("todo.restClientConfig")
public class RestClientConfig {
    @Value("${api.local-url}")
    private String LOCAL_URL;

    @Value("${server.servlet.contextPath}")
    private String CONTEXT_PATH;

    @Bean("todo.restClient")
    public RestClient restClient(){
        return RestClient.builder()
                .baseUrl(LOCAL_URL + CONTEXT_PATH)
                .build();
    }

}
