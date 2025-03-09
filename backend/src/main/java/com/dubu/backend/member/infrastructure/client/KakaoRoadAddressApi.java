package com.dubu.backend.member.infrastructure.client;

import com.dubu.backend.member.application.RoadAddressApi;
import com.dubu.backend.member.dto.response.AddressSearchResponse;
import com.dubu.backend.member.dto.response.KakaoPlaceApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoRoadAddressApi implements RoadAddressApi {
    private final KakaoApiProperties properties;

    @Override
    public List<AddressSearchResponse> search(String query) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .defaultHeader("Authorization", "KakaoAK " + properties.authorizationKey())
                .build();

        KakaoPlaceApiResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", query).build())
                .retrieve()
                .body(KakaoPlaceApiResponse.class);

        return response.documents().stream()
                .map(doc -> new AddressSearchResponse(
                        doc.roadAddress().buildingName(),
                        doc.roadAddress().addressName(),
                        Double.parseDouble(doc.roadAddress().x()),
                        Double.parseDouble(doc.roadAddress().y())
                ))
                .toList();
    }
}