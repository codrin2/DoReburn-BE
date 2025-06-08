package com.dubu.backend.plan.domain.repository.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PlanSearchCond(
        Long memberId,
        Boolean isPlanCompleted,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static PlanSearchCond of(Long memberId, boolean isPlanCompleted, LocalDateTime startTime, LocalDateTime endTime){
        return PlanSearchCond.builder()
                .memberId(memberId)
                .isPlanCompleted(isPlanCompleted)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
