package com.dubu.backend.plan.infrastructure;


import com.dubu.backend.plan.core.OdsayApiConfig;
import com.dubu.backend.plan.api.response.OdsayRouteApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class OdsayPathApi {
    private final OdsayApiConfig odsayApiConfig;

    public OdsayRouteApiResponse searchPublicTransportRoute(Double startX, Double startY, Double endX, Double endY) {
        RestClient restClient = RestClient.create("https://api.odsay.com/v1/api/searchPubTransPathT");

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apiKey", odsayApiConfig.apiKey())
                        .queryParam("SX", startX)
                        .queryParam("SY", startY)
                        .queryParam("EX", endX)
                        .queryParam("EY", endY)
                        .build())
                .retrieve()
                .body(OdsayRouteApiResponse.class);
    }
}