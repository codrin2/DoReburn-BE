package com.dubu.backend.member.infrastructure.client;

import com.dubu.backend.member.application.PlaceApi;
import com.dubu.backend.member.presentation.response.AddressSearchResponse;
import com.dubu.backend.member.presentation.response.NaverPlaceApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverPlaceApi implements PlaceApi {
    private final NaverApiProperties properties;

    @Override
    public List<AddressSearchResponse> search(String query) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/local.json")
                .defaultHeader("X-Naver-Client-Id", properties.clientId())
                .defaultHeader("X-Naver-Client-Secret", properties.clientSecret())
                .build();

        NaverPlaceApiResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", query)
                        .queryParam("display", 5)
                        .build())
                .retrieve()
                .body(NaverPlaceApiResponse.class);

        return response.places().stream()
                .map(place -> new AddressSearchResponse(
                        place.title().replaceAll("<.*?>", ""),
                        place.roadAddress(),
                        place.mapx() / 1_000_0000.0,
                        place.mapy() / 1_000_0000.0
                ))
                .toList();
    }
}