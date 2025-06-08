package com.dubu.backend.share.core.config;

import com.dubu.backend.core.interceptor.context.TokenContext;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration("share.RestClientConfig")
public class RestClientConfig {
    @Value("${api.local-url}")
    private String LOCAL_URL;

    @Bean("share.restClient")
    public RestClient restClient(){
        return RestClient.builder()
                .baseUrl(LOCAL_URL)
                .build();
    }
}
