package com.dubu.backend.plan.api.response;

import com.dubu.backend.plan.application.dto.DailyStatisticsResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DailyStatisticsResponse(
        LocalDate memberCreateDate,
        Integer totalTodoCount,
        Integer totalUsageTime,
        List<Feedback> feedbacks,
        List<CategoryTodoCount> categoryTodoCounts
) {
    public static DailyStatisticsResponse from(DailyStatisticsResult result){
        return DailyStatisticsResponse.builder()
                .memberCreateDate(result.memberCreateDate())
                .totalTodoCount(result.totalTodoCount())
                .totalUsageTime(result.totalUsageTime())
                .feedbacks(
                        result.feedbacks() != null ? result.feedbacks().stream()
                                .map(fi -> Feedback.of(fi.mood(), fi.memo()))
                                .toList() : null
                )
                .categoryTodoCount(
                        result.categoryTodoCount() != null ? result.categoryTodoCount().stream()
                                .map(c -> CategoryTodoCount.of(c.category(), c.todoCount()))
                                .toList() : null
                )
                .build();
    }

    private record Feedback(String mood, String memo){
        static Feedback of(String mood, String memo){
            return new Feedback(mood, memo);
        }
    }

    private record CategoryTodoCount(String category, int todoCount){
        static CategoryTodoCount of(String category, int todoCount){
            return new CategoryTodoCount(category, todoCount);
        }
    }
}
