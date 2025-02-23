package com.dubu.backend.plan.dto.response;

import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Route;
import com.dubu.backend.plan.domain.enums.TrafficType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record RouteSearchResponse(
        Boolean isRecentlyUsed,
        Integer totalTime,
        Integer totalSectionTime,
        List<PathDto> paths
) {
    public record PathDto(
            String trafficType,
            Integer sectionTime,
            Integer subwayCode,
            String busNumber,
            Integer busType,
            String startName,
            String endName
    ) {
    }

    public static RouteSearchResponse fromRoute(Route route, boolean isRecentlyUsed) {
        int totalSectionTime = route.getPaths().stream()
                .filter(path -> path.getTrafficType() != TrafficType.WALK)
                .mapToInt(Path::getSectionTime)
                .sum();

        // 각 Path 엔티티를 DTO로 변환 (pathOrder 기준 정렬)
        List<PathDto> pathDtoList = route.getPaths().stream()
                .sorted(Comparator.comparing(Path::getPathOrder))
                .map(p -> new PathDto(
                        p.getTrafficType().name(),
                        p.getSectionTime(),
                        p.getSubwayCode(),
                        p.getBusNumber(),
                        p.getBusType(),
                        p.getStartName(),
                        p.getEndName()
                ))
                .collect(Collectors.toList());

        return new RouteSearchResponse(isRecentlyUsed, route.getTotalTime(), totalSectionTime, pathDtoList);
    }
}