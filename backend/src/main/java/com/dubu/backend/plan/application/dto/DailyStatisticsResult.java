package com.dubu.backend.plan.application.dto;

import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.plan.domain.vo.DailyStats;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Builder
public record DailyStatisticsResult(
        LocalDate memberCreateDate,
        Integer totalTodoCount,
        Integer totalUsageTime,
        List<FeedbackInfo> feedbacks,
        List<CategoryTodoCount> categoryTodoCount){
    public static DailyStatisticsResult of(LocalDate memberCreateDate){
        return DailyStatisticsResult.builder()
                .memberCreateDate(memberCreateDate)
                .build();
    }

    public static DailyStatisticsResult from(LocalDate memberCreateDate, DailyStats stats, List<Feedback> feedbacks){
        return DailyStatisticsResult.builder()
                .memberCreateDate(memberCreateDate)
                .totalTodoCount(stats.totalTodoCount())
                .totalUsageTime((int) stats.totalUsageTime().toMinutes())
                .feedbacks(feedbacks.stream().map(f -> new FeedbackInfo(f.getMood().name(), f.getMemo())).toList())
                .categoryTodoCount(
                        stats.categoryCountMap().entrySet().stream()
                                .map(e -> new CategoryTodoCount(e.getKey(), e.getValue()))
                                .sorted(Comparator.comparing(CategoryTodoCount::todoCount).reversed()
                                        .thenComparing(CategoryTodoCount::category)
                                )
                                .toList()
                )
                .build();
    }

    public record FeedbackInfo(String mood, String memo){}

    public record CategoryTodoCount(String category, int todoCount){}
}
