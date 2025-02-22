package com.dubu.backend.statistic.dto.response;

import com.dubu.backend.plan.domain.enums.Mood;
import com.dubu.backend.statistic.service.collection.CategoryTodoStatistics;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WeekStatisticInfo(
        LocalDate memberCreateDate,
        List<DayAvailableTime> dayAvailableTimes,
        Integer totalTodoCount,
        Integer lastWeekDiff,
        Integer totalAvailableTime,
        List<MoodCountInfo> moodCounts,
        List<CategoryTodoInfo> categoryTodoCounts
) {
    public static WeekStatisticInfo of(LocalDate memberCreateDate) {
        return new WeekStatisticInfo(memberCreateDate, null, null, null, null, null, null);
    }
    public static WeekStatisticInfo of(LocalDate memberCreateDate, Map<LocalDate, Integer> dateUsageTime, int totalTodoCount, int lastWeekDiff, int totalAvailableTime, Map<Mood, Integer> moodCount, Map<String, CategoryTodoStatistics.TimeCount> categoryTodoTimeCount){
        return new WeekStatisticInfo(
                memberCreateDate,
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