package com.dubu.backend.member.infrastructure.client;

import com.dubu.backend.member.application.RoadAddressApi;
import com.dubu.backend.member.presentation.response.AddressSearchResponse;
import com.dubu.backend.member.presentation.response.KakaoPlaceApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoRoadAddressApi implements RoadAddressApi {
    @Value("${kakao.client.authorization-key}")
    private String authorizationKey;

    @Override
    public List<AddressSearchResponse> search(String query) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .defaultHeader("Authorization", "KakaoAK " + authorizationKey)
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