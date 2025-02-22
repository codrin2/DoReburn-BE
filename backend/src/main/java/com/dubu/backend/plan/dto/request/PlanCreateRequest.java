package com.dubu.backend.plan.dto.request;

import java.util.List;

public record PlanCreateRequest(
        Integer totalTime,
        Integer totalSectionTime,
        List<PlanCreateRequest.Path> paths
) {
    public record Path(
            String trafficType,
            Integer sectionTime,
            Integer subwayCode,
            String busNumber,
            Integer busType,
            String startName,
            String endName
    ) {
    }
}