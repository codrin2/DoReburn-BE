package com.dubu.backend.plan.application;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infrastructure.repository.MemberRepository;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Route;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.plan.domain.vo.PathIdentifier;
import com.dubu.backend.plan.dto.response.OdsayRouteApiResponse;
import com.dubu.backend.plan.dto.response.RouteSearchResponse;
import com.dubu.backend.plan.infra.client.OdsayApiClient;
import com.dubu.backend.plan.infra.repository.PathRepository;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.plan.infra.repository.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("RouteService 단위 테스트")
class RouteServiceTest {

    @Mock
    private OdsayApiClient odsayApiClient;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private PathRepository pathRepository;

    @InjectMocks
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("[createNewRoute] 경로(Route) 생성")
    class Describe_createNewRoute {

        @Test
        @DisplayName("주어진 좌표와 시간으로 Route를 생성 후 저장한다.")
        void it_creates_new_route() {
            // given
            Double startX = 127.0, startY = 37.0, endX = 126.9, endY = 37.1;
            int totalTime = 40;

            Route routeMock = mock(Route.class);
            given(routeRepository.save(any(Route.class))).willReturn(routeMock);

            // when
            Route result = routeService.createNewRoute(startX, startY, endX, endY, totalTime);

            // then
            assertThat(result).isNotNull();
            verify(routeRepository).save(any(Route.class));
        }
    }

    @Nested
    @DisplayName("[getRoutesByStartAndDestination] 경로 검색")
    class Describe_getRoutesByStartAndDestination {

        @Test
        @DisplayName("존재하는 memberId면, ODsay API 결과 없을 경우 DB의 Route 목록을 가져와 응답한다.")
        void it_returns_db_routes_when_api_result_is_empty() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(mockMember));

            Double startX = 127.0, startY = 37.0, endX = 126.9, endY = 37.1;

            // ODsay API 결과가 없다고 가정
            given(odsayApiClient.searchPublicTransportRoute(eq(startX), eq(startY), eq(endX), eq(endY)))
                    .willReturn(null);

            // DB에서 검색되는 Route
            Route mockRoute = mock(Route.class);
            when(mockRoute.getPaths()).thenReturn(List.of()); // 경로 Path는 없다고 가정
            when(mockRoute.getTotalTime()).thenReturn(40);

            List<Route> routeList = List.of(mockRoute);
            given(routeRepository.findAllWithPathsByCoordinates(startX, startY, endX, endY))
                    .willReturn(routeList);

            // when
            List<RouteSearchResponse> result = routeService.getRoutesByStartAndDestination(memberId, startX, startY, endX, endY);

            // then
            assertThat(result).hasSize(1);
            verify(routeRepository).findAllWithPathsByCoordinates(eq(startX), eq(startY), eq(endX), eq(endY));
        }

        @Test
        @DisplayName("memberId가 존재하지 않으면, MemberNotFoundException 예외 발생")
        void it_throws_MemberNotFoundException_if_member_not_found() {
            // given
            Long memberId = 9999L;
            given(memberRepository.findById(memberId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    routeService.getRoutesByStartAndDestination(memberId, 127.0, 37.0, 126.9, 37.1)
            ).isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("ODsay API 결과가 존재하면, API 경로 목록을 응답한다.")
        void it_returns_api_path_result() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(mockMember));

            Double startX = 127.0, startY = 37.0, endX = 126.9, endY = 37.1;

            // API 응답 Mock
            OdsayRouteApiResponse.Path apiPath = mock( OdsayRouteApiResponse.Path.class );
            List<OdsayRouteApiResponse.Path> apiPaths = List.of(apiPath);

            OdsayRouteApiResponse.Result apiResult = new OdsayRouteApiResponse.Result(
                    0, 0, 0, 0, 0, 0, 0, 0,
                    apiPaths
            );
            OdsayRouteApiResponse odsayResponse = new OdsayRouteApiResponse(apiResult);
            given(odsayApiClient.searchPublicTransportRoute(eq(startX), eq(startY), eq(endX), eq(endY)))
                    .willReturn(odsayResponse);

            // subPath, info mocking
            OdsayRouteApiResponse.Info infoMock = mock(OdsayRouteApiResponse.Info.class);
            when(infoMock.totalTime()).thenReturn(40);
            when(apiPath.info()).thenReturn(infoMock);
            when(apiPath.subPath()).thenReturn(Collections.emptyList());

            // when
            List<RouteSearchResponse> result = routeService.getRoutesByStartAndDestination(memberId, startX, startY, endX, endY);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).paths()).isEmpty();
            verify(odsayApiClient).searchPublicTransportRoute(startX, startY, endX, endY);
        }
    }

    @Nested
    @DisplayName("[findReusableRoute] 재사용 가능 경로 조회")
    class Describe_findReusableRoute {

        @Test
        @DisplayName("DB에서 동일한 좌표와 PathIdentifier를 갖는 Route가 있으면 반환한다.")
        void it_returns_reusable_route() {
            // given
            Double startX = 127.0, startY = 37.0, endX = 126.9, endY = 37.1;
            List<PathIdentifier> newPathIdentifiers = List.of(
                    new PathIdentifier("SUBWAY", "선릉", "역삼"),
                    new PathIdentifier("BUS", "역삼", "강남")
            );

            Route mockRoute = mock(Route.class);
            Path mockPath1 = mock(Path.class);
            when(mockPath1.getTrafficType()).thenReturn(TrafficType.SUBWAY);
            when(mockPath1.getStartName()).thenReturn("선릉");
            when(mockPath1.getEndName()).thenReturn("역삼");

            Path mockPath2 = mock(Path.class);
            when(mockPath2.getTrafficType()).thenReturn(TrafficType.BUS);
            when(mockPath2.getStartName()).thenReturn("역삼");
            when(mockPath2.getEndName()).thenReturn("강남");

            when(mockRoute.getPaths()).thenReturn(List.of(mockPath1, mockPath2));

            given(routeRepository.findAllWithPathsByCoordinates(startX, startY, endX, endY))
                    .willReturn(List.of(mockRoute));

            // when
            Route result = routeService.findReusableRoute(startX, startY, endX, endY, newPathIdentifiers);

            // then
            assertThat(result).isEqualTo(mockRoute);
        }

        @Test
        @DisplayName("DB에 재사용 가능한 Route가 없으면 null 반환")
        void it_returns_null_if_no_reusable_route() {
            // given
            Double startX = 127.0, startY = 37.0, endX = 126.9, endY = 37.1;
            List<PathIdentifier> newPathIdentifiers = List.of(
                    new PathIdentifier("SUBWAY", "선릉", "역삼")
            );
            given(routeRepository.findAllWithPathsByCoordinates(startX, startY, endX, endY))
                    .willReturn(List.of());

            // when
            Route result = routeService.findReusableRoute(startX, startY, endX, endY, newPathIdentifiers);

            // then
            assertThat(result).isNull();
        }
    }
}