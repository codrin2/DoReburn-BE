package com.dubu.backend.plan.domain.vo;

import lombok.Builder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record WeeklyTodoStats(
        Map<LocalDate, Duration> dayUsageTimeMap,
        int totalTodoCount,
        Duration totalUsageTime,
        Duration lastWeekDiff,
        Map<String, TodoStats> categoryTodoStats
) {
    public static WeeklyTodoStats of(Map<LocalDate, Duration> dayUsageTimeMap,
                                     int totalTodoCount,
                                     Duration totalUsageTime,
                                     Duration lastWeekDiff,
                                     Map<String, Integer> categoryCountMap,
                                     Map<String, Duration> categoryUsageTimeMap){
        return WeeklyTodoStats.builder()
                .dayUsageTimeMap(Map.copyOf(dayUsageTimeMap))
                .totalTodoCount(totalTodoCount)
                .totalUsageTime(totalUsageTime)
                .lastWeekDiff(lastWeekDiff)
                .categoryTodoStats(
                        categoryCountMap.entrySet()
                                .stream()
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                        e -> new TodoStats(e.getValue(), categoryUsageTimeMap.get(e.getKey()))))
                )
                .build();
    }
    public record TodoStats(
            int count,
            Duration usageTime
    ){}
}
