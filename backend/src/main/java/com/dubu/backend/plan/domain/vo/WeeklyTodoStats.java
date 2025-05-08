package com.dubu.backend.plan.domain.vo;

import com.dubu.backend.plan.domain.Category;
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
        Map<Category, TodoStats> categoryTodoStats
) {
    public static WeeklyTodoStats of(Map<LocalDate, Duration> dayUsageTimeMap,
                                     int totalTodoCount,
                                     Duration totalUsageTime,
                                     Duration lastWeekDiff,
                                     Map<Category, Integer> categoryCountMap,
                                     Map<Category, Duration> categoryUsageTimeMap){
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
