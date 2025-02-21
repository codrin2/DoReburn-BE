package com.dubu.backend.statistic.dto.response;

import com.dubu.backend.plan.domain.enums.Mood;
import com.dubu.backend.statistic.service.collection.CategoryTodoStatistics;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public record WeekStatisticInfo(
        List<DayAvailableTime> dayAvailableTimes,
        int totalTodoCount,
        int lastWeekDiff,
        int totalAvailableTime,
        List<MoodCountInfo> moodCounts,
        List<CategoryTodoInfo> categoryTodoCounts
) {
    public static WeekStatisticInfo of(Map<LocalDate, Integer> dateUsageTime, int totalTodoCount, int lastWeekDiff, int totalAvailableTime, Map<Mood, Integer> moodCount, Map<String, CategoryTodoStatistics.TimeCount> categoryTodoTimeCount){
        return new WeekStatisticInfo(
                DayAvailableTime.fromDateUsageTime(dateUsageTime),
                totalTodoCount,
                lastWeekDiff,
                totalAvailableTime,
                moodCount.entrySet()
                        .stream()
                        .sorted(Comparator.comparingInt(entry -> entry.getKey().ordinal()))
                        .map(entry -> MoodCountInfo.of(entry.getKey().name(), entry.getValue()))
                        .toList(),
                CategoryTodoInfo.fromCategoryTodoTimeCount(categoryTodoTimeCount)
        );
    }

    record DayAvailableTime(LocalDate date, int availableTime){
        private static List<DayAvailableTime> fromDateUsageTime(Map<LocalDate, Integer> dateUsageTime){
            return dateUsageTime.entrySet()
                            .stream()
                            .map(entry -> new DayAvailableTime(entry.getKey(), entry.getValue()))
                            .toList();
        }
    }
}