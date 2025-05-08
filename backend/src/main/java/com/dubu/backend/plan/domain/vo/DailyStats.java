package com.dubu.backend.plan.domain.vo;

import com.dubu.backend.plan.domain.Category;
import lombok.Builder;

import java.time.Duration;
import java.util.Map;

@Builder
public record DailyStats(
        int totalTodoCount,
        Duration totalUsageTime,
        Map<Category, Integer> categoryCountMap
) {
    public static DailyStats of(Duration totalUsageTime, int totalTodoCount, Map<Category, Integer> categoryCountMap){
        return DailyStats.builder()
                .totalUsageTime(totalUsageTime)
                .totalTodoCount(totalTodoCount)
                .categoryCountMap(Map.copyOf(categoryCountMap))
                .build();
    }
}
