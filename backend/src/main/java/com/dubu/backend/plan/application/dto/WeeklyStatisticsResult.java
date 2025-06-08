package com.dubu.backend.plan.application.dto;

import com.dubu.backend.plan.domain.vo.WeeklyFeedbackStats;
import com.dubu.backend.plan.domain.vo.WeeklyTodoStats;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Builder
public record WeeklyStatisticsResult(
        LocalDate memberCreateDate,
        List<DayAvailableTime> dayAvailableTimes,
        Integer totalTodoCount,
        Integer totalAvailableTime,
        Integer lastWeekDiff,
        List<MoodCount> moodCounts,
        List<CategoryTodoStats> categoryTodoStats
) {
    public static WeeklyStatisticsResult of(LocalDate memberCreateDate){
        return WeeklyStatisticsResult.builder()
                .memberCreateDate(memberCreateDate)
                .build();
    }

    public static WeeklyStatisticsResult from(LocalDate memberCreateDate, WeeklyTodoStats todoStats, WeeklyFeedbackStats feedbackStats) {
        return WeeklyStatisticsResult.builder()
                .memberCreateDate(memberCreateDate)
                .dayAvailableTimes(
                        todoStats.dayUsageTimeMap().entrySet().stream()
                                .map(entry -> new DayAvailableTime(entry.getKey(), (int)entry.getValue().toMinutes()))
                                .sorted(Comparator.comparing(DayAvailableTime::date))
                                .toList()
                )
                .totalTodoCount(todoStats.totalTodoCount())
                .totalAvailableTime((int)todoStats.totalUsageTime().toMinutes())
                .lastWeekDiff((int)todoStats.lastWeekDiff().toMinutes())
                .moodCounts(
                        feedbackStats.moodCountMap().entrySet().stream()
                                .sorted(Comparator.comparing(entry -> entry.getKey().ordinal()))
                                .map(entry -> new MoodCount(entry.getKey().name(), entry.getValue()))
                                .toList()
                )
                .categoryTodoStats(
                        todoStats.categoryTodoStats().entrySet().stream()
                                .sorted(Comparator.comparing(entry -> entry.getValue().usageTime().toMinutes(), Comparator.reverseOrder()))
                                .map(entry -> new CategoryTodoStats(entry.getKey(), (int)entry.getValue().usageTime().toMinutes(), entry.getValue().count()))
                                .toList()
                )
                .build();
    }

    public record DayAvailableTime(LocalDate date, int availableTime){}

    public record MoodCount(String mood, int count){}

    public record CategoryTodoStats(String category, int usageTime, int count){}
}
