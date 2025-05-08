package com.dubu.backend.plan.api.response;

import com.dubu.backend.plan.application.dto.WeeklyStatisticsResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record WeeklyStatisticsResponse(
        LocalDate memberCreateDate,
        List<DayAvailableTime> dayAvailableTimes,
        Integer totalTodoCount,
        Integer totalAvailableTime,
        Integer lastWeekDiff,
        List<MoodCount> moodCounts,
        List<CategoryTodoCounts> categoryTodoCounts
) {
    public static WeeklyStatisticsResponse from(WeeklyStatisticsResult result) {
        return WeeklyStatisticsResponse.builder()
                .memberCreateDate(result.memberCreateDate())
                .dayAvailableTimes(
                        result.dayAvailableTimes() != null ? result.dayAvailableTimes().stream()
                                .map(d -> DayAvailableTime.of(d.date(), d.availableTime()))
                                .toList() : null
                )
                .totalTodoCount(result.totalTodoCount())
                .totalAvailableTime(result.totalAvailableTime())
                .lastWeekDiff(result.lastWeekDiff())
                .moodCounts(
                        result.moodCounts() != null ? result.moodCounts().stream()
                                .map(mc -> MoodCount.of(mc.mood(), mc.count()))
                                .toList() : null
                )
                .categoryTodoCounts(
                        result.categoryTodoStats() != null ?result.categoryTodoStats().stream()
                                .map(c -> CategoryTodoCounts.of(c.category(), c.usageTime(), c.count()))
                                .toList() : null
                )
                .build();
    }

    private record DayAvailableTime(LocalDate date, int availableTime){
        static DayAvailableTime of(LocalDate date ,int availableTime){
            return new DayAvailableTime(date, availableTime);
        }
    }

    private record MoodCount(String mood, int count){
        static MoodCount of(String mood, int count){
            return new MoodCount(mood, count);
        }
    }

    private record CategoryTodoCounts(String category, int usageTime, int count){
        static CategoryTodoCounts of(String category, int usageTime, int count) {
            return new CategoryTodoCounts(category, usageTime, count);
        }
    }
}
