package com.dubu.backend.plan.application;

import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.plan.domain.vo.PathIdentifier;
import com.dubu.backend.plan.api.response.OdsayRouteApiResponse;
import com.dubu.backend.plan.api.response.RouteSearchResponse;
import com.dubu.backend.plan.infrastructure.OdsayPathApi;
import com.dubu.backend.plan.domain.repository.SubPathRepository;
import com.dubu.backend.plan.domain.repository.PlanRepository;
import com.dubu.backend.plan.domain.repository.PathRepository;
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
class SubPathFacadeTest {

    @Mock
    private OdsayPathApi odsayPathApi;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private PathRepository pathRepository;
    @Mock
    private SubPathRepository subPathRepository;

    @InjectMocks
    private PathFacade pathFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("[createNewRoute] 경로(Route) 생성")
    class Describe_createNewPath {

        @Test
        @DisplayName("주어진 좌표와 시간으로 Route를 생성 후 저장한다.")
        void it_creates_new_route() {
            // given
            Double startX = 127.0, startY = 37.0, endX = 126.9, endY = 37.1;
            int totalTime = 40;

            Path pathMock = mock(Path.class);
            given(pathRepository.save(any(Path.class))).willReturn(pathMock);

            // when
            Path result = pathFacade.createNewRoute(startX, startY, endX, endY, totalTime);

            // then
            assertThat(result).isNotNull();
            verify(pathRepository).save(any(Path.class));
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
            given(odsayPathApi.searchPublicTransportRoute(eq(startX), eq(startY), eq(endX), eq(endY)))
                    .willReturn(null);

            // DB에서 검색되는 Route
            Path mockPath = mock(Path.class);
            when(mockPath.getSubPaths()).thenReturn(List.of()); // 경로 Path는 없다고 가정
            when(mockPath.getTotalTime()).thenReturn(40);

            List<Path> pathList = List.of(mockPath);
            given(pathRepository.findAllWithPathsByCoordinates(startX, startY, endX, endY))
                    .willReturn(pathList);

            // when
            List<RouteSearchResponse> result = pathFacade.getRoutesByStartAndDestination(memberId, startX, startY, endX, endY);

            // then
            assertThat(result).hasSize(1);
            verify(pathRepository).findAllWithPathsByCoordinates(eq(startX), eq(startY), eq(endX), eq(endY));
        }

        @Test
        @DisplayName("memberId가 존재하지 않으면, MemberNotFoundException 예외 발생")
        void it_throws_MemberNotFoundException_if_member_not_found() {
            // given
            Long memberId = 9999L;
            given(memberRepository.findById(memberId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    pathFacade.getRoutesByStartAndDestination(memberId, 127.0, 37.0, 126.9, 37.1)
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
            given(odsayPathApi.searchPublicTransportRoute(eq(startX), eq(startY), eq(endX), eq(endY)))
                    .willReturn(odsayResponse);

            // subPath, info mocking
            OdsayRouteApiResponse.Info infoMock = mock(OdsayRouteApiResponse.Info.class);
            when(infoMock.totalTime()).thenReturn(40);
            when(apiPath.info()).thenReturn(infoMock);
            when(apiPath.subPath()).thenReturn(Collections.emptyList());

            // when
            List<RouteSearchResponse> result = pathFacade.getRoutesByStartAndDestination(memberId, startX, startY, endX, endY);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).paths()).isEmpty();
            verify(odsayPathApi).searchPublicTransportRoute(startX, startY, endX, endY);
        }
    }

    @Nested
    @DisplayName("[findReusableRoute] 재사용 가능 경로 조회")
    class Describe_findReusablePath {

        @Test
        @DisplayName("DB에서 동일한 좌표와 PathIdentifier를 갖는 Route가 있으면 반환한다.")
        void it_returns_reusable_route() {
            // given
            Double startX = 127.0, startY = 37.0, endX = 126.9, endY = 37.1;
            List<PathIdentifier> newPathIdentifiers = List.of(
                    new PathIdentifier("SUBWAY", "선릉", "역삼"),
                    new PathIdentifier("BUS", "역삼", "강남")
            );

            Path mockPath = mock(Path.class);
            SubPath mockSubPath1 = mock(SubPath.class);
            when(mockSubPath1.getTrafficType()).thenReturn(TrafficType.SUBWAY);
            when(mockSubPath1.getStartName()).thenReturn("선릉");
            when(mockSubPath1.getEndName()).thenReturn("역삼");

            SubPath mockSubPath2 = mock(SubPath.class);
            when(mockSubPath2.getTrafficType()).thenReturn(TrafficType.BUS);
            when(mockSubPath2.getStartName()).thenReturn("역삼");
            when(mockSubPath2.getEndName()).thenReturn("강남");

            when(mockPath.getSubPaths()).thenReturn(List.of(mockSubPath1, mockSubPath2));

            given(pathRepository.findAllWithPathsByCoordinates(startX, startY, endX, endY))
                    .willReturn(List.of(mockPath));

            // when
            Path result = pathFacade.findReusableRoute(startX, startY, endX, endY, newPathIdentifiers);

            // then
            assertThat(result).isEqualTo(mockPath);
        }

        @Test
        @DisplayName("DB에 재사용 가능한 Route가 없으면 null 반환")
        void it_returns_null_if_no_reusable_route() {
            // given
            Double startX = 127.0, startY = 37.0, endX = 126.9, endY = 37.1;
            List<PathIdentifier> newPathIdentifiers = List.of(
                    new PathIdentifier("SUBWAY", "선릉", "역삼")
            );
            given(pathRepository.findAllWithPathsByCoordinates(startX, startY, endX, endY))
                    .willReturn(List.of());

            // when
            Path result = pathFacade.findReusableRoute(startX, startY, endX, endY, newPathIdentifiers);

            // then
            assertThat(result).isNull();
        }
    }
}