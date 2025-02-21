package com.dubu.backend.statistic.dto.response;

import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.statistic.service.collection.CategoryTodoStatistics;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record DayStatisticInfo(
        LocalDate memberCreateDate,
        Integer totalUsageTime,
        Integer totalTodoCount,
        List<FeedbackInfo> feedbacks,
        List<CategoryTodoInfo> categoryTodoCounts
) {
    public static DayStatisticInfo of(LocalDate memberCreateDate){
        return new DayStatisticInfo(memberCreateDate, null, null, null, null);
    }
    public static DayStatisticInfo of(LocalDate memberCreateDate, int totalUsageTime, int totalTodoCount, List<Feedback> feedbacks, Map<String, CategoryTodoStatistics.TimeCount> categoryTodoCount){

        return new DayStatisticInfo(
                memberCreateDate,
                totalUsageTime,
                totalTodoCount,
                FeedbackInfo.fromFeedbacks(feedbacks),
                CategoryTodoInfo.fromCategoryTodoTimeCount(categoryTodoCount)
        );
    }

    public record FeedbackInfo(String mood, String memo) {
        public static List<FeedbackInfo> fromFeedbacks(List<Feedback> feedbacks){
            return feedbacks.stream()
                    .map(f -> new FeedbackInfo(f.getMood().name(), f.getMemo()))
                    .toList();
        }
    }
}