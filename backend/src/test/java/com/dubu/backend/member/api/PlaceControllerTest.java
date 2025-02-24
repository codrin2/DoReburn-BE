package com.dubu.backend.member.api;

import com.dubu.backend.global.config.WebConfig;
import com.dubu.backend.global.interceptor.TokenInterceptor;
import com.dubu.backend.member.application.PlaceService;
import com.dubu.backend.member.dto.response.PlaceSearchResponse;
import com.dubu.backend.member.exception.KakaoApiServerException;
import com.dubu.backend.member.exception.NaverApiServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PlaceController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebConfig.class,
                        TokenInterceptor.class
                })
        })
@DisplayName("PlaceController 테스트")
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaceService placeService;

    @Nested
    @DisplayName("[GET /places/search?query=XXX] 장소 검색")
    class Describe_searchPlaces {

        @Test
        @DisplayName("정상적인 query 파라미터로 요청하면, 200 OK와 함께 장소 검색 결과를 반환한다.")
        void it_returns_places() throws Exception {
            // given
            String query = "카페";
            List<PlaceSearchResponse> mockResponse = List.of(
                    new PlaceSearchResponse("카페 홍길동", "서울 강남구 테헤란로 427", 127.0453733, 37.5048676),
                    new PlaceSearchResponse("카페 이순신", "서울 강남구 봉은사로 109", 127.0594931, 37.5057432)
            );

            BDDMockito.given(placeService.searchPlaces(query)).willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/places/search")
                            .param("query", query)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].title").value("카페 홍길동"))
                    .andExpect(jsonPath("$.data[1].roadAddress").value("서울 강남구 봉은사로 109"));
        }

        @Test
        @DisplayName("네이버 API 서버 장애 시, 503 응답을 반환한다.")
        void it_returns_503_when_naver_api_unavailable() throws Exception {
            // given
            String query = "카페";
            Mockito.doThrow(new NaverApiServerException())
                    .when(placeService).searchPlaces(eq(query));

            // when & then
            mockMvc.perform(get("/places/search")
                            .param("query", query)
                    )
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.errorCode").value("NAVER_SERVICE_UNAVAILABLE"));
        }

        @Test
        @DisplayName("카카오 API 서버 장애 시, 503 응답을 반환한다.")
        void it_returns_503_when_kakao_api_unavailable() throws Exception {
            // given
            String query = "카페";
            Mockito.doThrow(new KakaoApiServerException())
                    .when(placeService).searchPlaces(eq(query));

            // when & then
            mockMvc.perform(get("/places/search")
                            .param("query", query)
                    )
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.errorCode").value("KAKAO_SERVICE_UNAVAILABLE"));
        }
    }
}