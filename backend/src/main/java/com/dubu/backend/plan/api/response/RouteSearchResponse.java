package com.dubu.backend.plan.api.response;

import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.SubPath;
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

    public static RouteSearchResponse fromRoute(Path path, boolean isRecentlyUsed) {
        int totalSectionTime = path.getSubPaths().stream()
                .filter(subPath ->  subPath.getTrafficType() != TrafficType.WALK)
                .mapToInt(SubPath::getSectionTime)
                .sum();

        // 각 Path 엔티티를 DTO로 변환 (pathOrder 기준 정렬)
        List<PathDto> pathDtoList = path.getSubPaths().stream()
                .sorted(Comparator.comparing(SubPath::getPathOrder))
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

        return new RouteSearchResponse(isRecentlyUsed, path.getTotalTime(), totalSectionTime, pathDtoList);
    }
}