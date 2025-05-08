package com.dubu.backend.plan.domain.repository.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PlanSearchCond(
        Boolean isPlanCompleted,
        Boolean isTodoCompleted,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static PlanSearchCond of(boolean isPlanCompleted, boolean isTodoCompleted, LocalDateTime startTime, LocalDateTime endTime){
        return PlanSearchCond.builder()
                .isPlanCompleted(isPlanCompleted)
                .isTodoCompleted(isTodoCompleted)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
