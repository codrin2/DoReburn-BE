package com.dubu.backend.plan.application;

import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.Route;
import com.dubu.backend.plan.domain.vo.PathIdentifier;
import com.dubu.backend.plan.dto.response.OdsayRouteApiResponse;
import com.dubu.backend.plan.dto.response.RouteSearchResponse;
import com.dubu.backend.plan.infra.client.OdsayApiClient;
import com.dubu.backend.plan.infra.repository.PathRepository;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.plan.infra.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 같은 의미인 명칭 정리
 * 두리번 사용 = ODsay 사용
 * Route = Path
 * Path = SubPath
 */
@Service
@RequiredArgsConstructor
public class RouteService {
    private final OdsayApiClient odsayApiClient;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final RouteRepository routeRepository;
    private final PathRepository pathRepository;

    @Transactional
    public Route createNewRoute(Double startX, Double startY,
                                Double endX, Double endY,
                                Integer totalTime
    ) {
        Route newRoute = Route.createRoute(startX, startY, endX, endY, totalTime);
        return routeRepository.save(newRoute);
    }

    /**
     * 출발지, 도착지 좌표로 경로를 조회하여,
     * DB 또는 ODsay API 결과에 따라 RouteSearchResponse DTO 목록을 반환합니다.
     */
    @Transactional(readOnly = true)
    public List<RouteSearchResponse> getRoutesByStartAndDestination(Long memberId, Double startX, Double startY, Double endX, Double endY) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 사용자가 최근 이용한 경로 조회
        List<PathIdentifier> recentlyUsedRoute = loadRecentlyUsedRoute(memberId);

        OdsayRouteApiResponse odsayRouteApiResponse = odsayApiClient.searchPublicTransportRoute(startX, startY, endX, endY);

        List<RouteSearchResponse> response = new ArrayList<>();
        if (odsayRouteApiResponse == null || odsayRouteApiResponse.result() == null || odsayRouteApiResponse.result().path() == null) {
            // API 결과가 없는 경우, DB에서 좌표에 해당하는 Route를 조회 (경로와 관련된 Path도 함께 로딩)
            List<Route> routeList = routeRepository.findAllWithPathsByCoordinates(startX, startY, endX, endY);
            routeList.forEach(route -> {
                // DB에 저장된 경로와 최근 사용 경로가 동일한지 확인
                boolean isRecentlyUsed = isSameAsSavedRoute(route, recentlyUsedRoute);
                RouteSearchResponse dto = RouteSearchResponse.fromRoute(route, isRecentlyUsed);
                response.add(dto);
            });
        } else {
            for (OdsayRouteApiResponse.Path apiPath : odsayRouteApiResponse.result().path()) {
                // API에서 받은 경로와 최근 사용 경로가 동일한지 확인
                boolean isRecentlyUsed = isSameAsRecentlyUsedRoute(apiPath, recentlyUsedRoute);
                RouteSearchResponse routeDto = convertApiPathToRoute(apiPath, isRecentlyUsed);
                response.add(routeDto);
            }
        }

        // 최근 사용 여부 내림차순, 그 다음 totalTime 오름차순 정렬
        response.sort(Comparator
                .comparing(RouteSearchResponse::isRecentlyUsed, Comparator.reverseOrder())
                .thenComparing(RouteSearchResponse::totalTime)
        );

        return response;
    }

    @Transactional(readOnly = true)
    public Route findReusableRoute(Double startX, Double startY, Double endX, Double endY,
                                   List<PathIdentifier> newPathIdentifiers) {

        // 좌표가 같은 Route들 모두 조회
        List<Route> existingRoutes = routeRepository.findAllWithPathsByCoordinates(startX, startY, endX, endY);

        // 각 Route의 PathIdentifier 목록 추출 후, newPathIdentifiers를 포함하는지 검사
        return existingRoutes.stream()
                .filter(routeCandidate -> {
                    List<PathIdentifier> existingPathIdentifiers = routeCandidate.getPaths().stream()
                            .map(p -> new PathIdentifier(
                                    p.getTrafficType().name(),
                                    p.getStartName(),
                                    p.getEndName()
                            ))
                            .distinct()
                            .toList();
                    return existingPathIdentifiers.containsAll(newPathIdentifiers);
                })
                .findFirst()
                .orElse(null);
    }

    private List<PathIdentifier> loadRecentlyUsedRoute(Long memberId) {
        Plan latestPlan = planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElse(null);

        if (latestPlan == null) {
            return List.of();
        }

        List<Path> pathList = pathRepository.findByPlanIdOrderByPathOrderAsc(latestPlan.getId());

        return pathList.stream()
                .map(p -> {
                    String startName = p.getStartName();
                    String endName = p.getEndName();

                    return new PathIdentifier(
                            p.getTrafficType().name(),
                            startName,
                            endName
                    );
                })
                .toList();
    }

    /**
     * DB에서 조회한 Route와 최근 사용 Route가 동일한지 확인
     */
    private boolean isSameAsSavedRoute(Route route, List<PathIdentifier> recentlyUsedRoute) {
        List<PathIdentifier> routePathIdentifiers = route.getPaths().stream()
                .sorted(Comparator.comparing(Path::getPathOrder)) // pathOrder 기준 정렬
                .map(p -> new PathIdentifier(
                        p.getTrafficType().name(),
                        p.getStartName(),
                        p.getEndName()
                ))
                .toList();

        return isSameRoute(routePathIdentifiers, recentlyUsedRoute);
    }

    /**
     * API 응답을 변환한 Route와 최근 사용 Route가 동일한지 확인
     */
    private boolean isSameAsRecentlyUsedRoute(OdsayRouteApiResponse.Path apiPath,
                                              List<PathIdentifier> recentlyUsedRoute) {
        List<PathIdentifier> currentRouteKeys = extractRouteKeys(apiPath);
        return isSameRoute(currentRouteKeys, recentlyUsedRoute);
    }

    /**
     * 두 개의 PathIdentifier 리스트가 동일한지 비교
     * (길이가 다르면 false, 순서대로 하나씩 비교)
     */
    private boolean isSameRoute(List<PathIdentifier> routePathIdentifiers, List<PathIdentifier> recentlyUsedRoute) {
        if (routePathIdentifiers.size() != recentlyUsedRoute.size()) {
            return false;
        }
        for (int i = 0; i < routePathIdentifiers.size(); i++) {
            if (!routePathIdentifiers.get(i).equals(recentlyUsedRoute.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * ODsay API 경로(apiPath)에서 각 SubPath의 (trafficType, startName, endName) 정보를 추출
     * 지하철의 경우 "역"을 붙여서 반환(서울역 제외)
     */
    private List<PathIdentifier> extractRouteKeys(OdsayRouteApiResponse.Path apiPath) {
        List<PathIdentifier> result = new ArrayList<>();
        for (OdsayRouteApiResponse.SubPath subPath : apiPath.subPath()) {
            int tType = subPath.trafficType();
            if (tType == 1 || tType == 2) {
                String trafficType = (tType == 1) ? "SUBWAY" : "BUS";

                String startName = subPath.startName();
                String endName = subPath.endName();
                if (tType == 1) { // 지하철
                    startName = Objects.equals(subPath.startName(), "서울역") ? subPath.startName() : subPath.startName() + "역";
                    endName = Objects.equals(subPath.endName(), "서울역") ? subPath.endName() : subPath.endName() + "역";
                }
                result.add(new PathIdentifier(trafficType, startName, endName));
            }
        }
        return result;
    }

    private RouteSearchResponse convertApiPathToRoute(OdsayRouteApiResponse.Path apiPath, boolean isRecentlyUsed) {
        OdsayRouteApiResponse.Info info = apiPath.info();

        int totalTime = info.totalTime();
        int totalSectionTime = 0;

        List<RouteSearchResponse.PathDto> pathDtoList = new ArrayList<>();

        // SubPath(=ODsay) → Path(=두리번)Dto 변환
        for (OdsayRouteApiResponse.SubPath subPath : apiPath.subPath()) {
            RouteSearchResponse.PathDto dtoPath = convertApiSubPathToPathDto(subPath);

            if ("BUS".equals(dtoPath.trafficType()) || "SUBWAY".equals(dtoPath.trafficType())) {
                totalSectionTime += dtoPath.sectionTime();
            }
            pathDtoList.add(dtoPath);
        }

        return new RouteSearchResponse(isRecentlyUsed, totalTime, totalSectionTime, pathDtoList);
    }

    private RouteSearchResponse.PathDto convertApiSubPathToPathDto(OdsayRouteApiResponse.SubPath subPath) {
        int tType = subPath.trafficType();
        String trafficType = switch (tType) {
            case 1 -> "SUBWAY";
            case 2 -> "BUS";
            case 3 -> "WALK";
            default -> "UNKNOWN";
        };

        Integer subwayCode = null;
        String busNumber = null;
        Integer busType = null;
        String startName = null;
        String endName = null;

        if (subPath.lane() != null && !subPath.lane().isEmpty()) {
            OdsayRouteApiResponse.Lane lane = subPath.lane().get(0);
            if ("SUBWAY".equals(trafficType)) {
                subwayCode = lane.subwayCode();
                startName = Objects.equals(subPath.startName(), "서울역") ? subPath.startName() : subPath.startName() + "역";
                endName = Objects.equals(subPath.endName(), "서울역") ? subPath.endName() : subPath.endName() + "역";
            } else if ("BUS".equals(trafficType)) {
                busNumber = lane.busNo();
                busType = lane.type();
                startName = subPath.startName();
                endName = subPath.endName();
            }
        }

        return new RouteSearchResponse.PathDto(
                trafficType,
                subPath.sectionTime(),
                subwayCode,
                busNumber,
                busType,
                startName,
                endName
        );
    }
}