package com.dubu.backend.plan.api;

import com.dubu.backend.core.config.WebConfig;
import com.dubu.backend.core.interceptor.TokenInterceptor;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.plan.application.RouteService;
import com.dubu.backend.plan.dto.response.RouteSearchResponse;
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

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RouteController만 로드하여 MockMvc로 테스트.
 */
@WebMvcTest(
        controllers = RouteController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebConfig.class,
                        TokenInterceptor.class
                })
        })
@DisplayName("RouteController 테스트")
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteService routeService;

    @Nested
    @DisplayName("[GET /routes/search] 경로 검색")
    class Describe_routeSearch {

        @Test
        @DisplayName("정상적인 요청이면, 200 OK와 함께 경로 검색 결과를 반환한다.")
        void it_returns_route_search_result() throws Exception {
            // given
            Long memberId = 1L;
            List<RouteSearchResponse> mockResponse = List.of(
                    new RouteSearchResponse(true, 40, 40, List.of())
            );
            BDDMockito.given(routeService.getRoutesByStartAndDestination(eq(memberId), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                    .willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/routes/search")
                            .requestAttr("memberId", memberId)
                            .param("startX", "127.0")
                            .param("startY", "37.0")
                            .param("endX", "126.9")
                            .param("endY", "37.1")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].isRecentlyUsed").value(true))
                    .andExpect(jsonPath("$.data[0].totalTime").value(40));
        }

        @Test
        @DisplayName("존재하지 않는 회원이면, 404 Not Found를 반환한다.")
        void it_returns_404_when_member_not_found() throws Exception {
            Long memberId = 9999L;
            Mockito.doThrow(new MemberNotFoundException(memberId))
                    .when(routeService).getRoutesByStartAndDestination(eq(memberId), anyDouble(), anyDouble(), anyDouble(), anyDouble());

            mockMvc.perform(get("/routes/search")
                            .requestAttr("memberId", memberId)
                            .param("startX", "127.0")
                            .param("startY", "37.0")
                            .param("endX", "126.9")
                            .param("endY", "37.1")
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"));
        }
    }
}