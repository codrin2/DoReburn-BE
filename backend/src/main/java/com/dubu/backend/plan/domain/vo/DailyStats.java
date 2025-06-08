package com.dubu.backend.plan.domain.vo;

import lombok.Builder;

import java.time.Duration;
import java.util.Map;

@Builder
public record DailyStats(
        int totalTodoCount,
        Duration totalUsageTime,
        Map<String, Integer> categoryCountMap
) {
    public static DailyStats of(Duration totalUsageTime, int totalTodoCount, Map<String, Integer> categoryCountMap){
        return DailyStats.builder()
                .totalUsageTime(totalUsageTime)
                .totalTodoCount(totalTodoCount)
                .categoryCountMap(Map.copyOf(categoryCountMap))
                .build();
    }
}
