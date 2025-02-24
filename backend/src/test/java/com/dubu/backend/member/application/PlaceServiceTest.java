package com.dubu.backend.member.application;

import com.dubu.backend.member.dto.response.KakaoPlaceApiResponse;
import com.dubu.backend.member.dto.response.KakaoPlaceApiResponse.DocumentsResponse;
import com.dubu.backend.member.dto.response.KakaoPlaceApiResponse.DocumentsResponse.RoadAddressResponse;
import com.dubu.backend.member.dto.response.NaverPlaceApiResponse;
import com.dubu.backend.member.dto.response.NaverPlaceApiResponse.NaverPlace;
import com.dubu.backend.member.dto.response.PlaceSearchResponse;
import com.dubu.backend.member.infra.client.KakaoPlaceApiClient;
import com.dubu.backend.member.infra.client.NaverPlaceApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("PlaceService 단위 테스트")
class PlaceServiceTest {

    @Mock
    private NaverPlaceApiClient naverPlaceApiClient;
    @Mock
    private KakaoPlaceApiClient kakaoPlaceApiClient;

    @InjectMocks
    private PlaceService placeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("[searchPlaces(query)] 장소 검색")
    class Describe_searchPlaces {

        @Test
        @DisplayName("naver/kakao 모두 정상 응답이면, 라운드 로빈 방식으로 결과가 합쳐진 리스트를 반환한다.")
        void it_returns_merged_places_in_round_robin() {
            // given
            String query = "카페";

            // 네이버 Mock 응답
            NaverPlace naver1 = new NaverPlace("네이버카페1", "서울 강남구 도산대로 1", 1270000, 370000);
            NaverPlace naver2 = new NaverPlace("네이버카페2", "서울 강남구 도산대로 2", 1270500, 370500);

            NaverPlaceApiResponse naverMockResponse = new NaverPlaceApiResponse(List.of(naver1, naver2));

            // 카카오 Mock 응답
            RoadAddressResponse kakaoRoad1 = new RoadAddressResponse("서울 강남구 학동로 1", "카카오카페1", "127.1", "37.1");
            RoadAddressResponse kakaoRoad2 = new RoadAddressResponse("서울 강남구 학동로 2", "카카오카페2", "127.2", "37.2");

            DocumentsResponse kakaoDoc1 = new DocumentsResponse(kakaoRoad1);
            DocumentsResponse kakaoDoc2 = new DocumentsResponse(kakaoRoad2);

            KakaoPlaceApiResponse kakaoMockResponse = new KakaoPlaceApiResponse(List.of(kakaoDoc1, kakaoDoc2));

            given(naverPlaceApiClient.searchPlaces(query)).willReturn(naverMockResponse);
            given(kakaoPlaceApiClient.searchPlaces(query)).willReturn(kakaoMockResponse);

            // when
            List<PlaceSearchResponse> result = placeService.searchPlaces(query);

            // then
            assertThat(result).hasSize(4);
            // 라운드 로빈 순서 확인: naver1, kakao1, naver2, kakao2
            assertThat(result.get(0).title()).isEqualTo("네이버카페1");
            assertThat(result.get(1).title()).isEqualTo("카카오카페1");
            assertThat(result.get(2).title()).isEqualTo("네이버카페2");
            assertThat(result.get(3).title()).isEqualTo("카카오카페2");
        }
    }
}