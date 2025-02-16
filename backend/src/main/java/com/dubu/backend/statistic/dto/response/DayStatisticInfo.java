package com.dubu.backend.statistic.dto.response;

import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.statistic.service.collection.CategoryTodoStatistics;

import java.util.List;
import java.util.Map;

public record DayStatisticInfo(
        int totalMoveTime,
        int totalUsageTime,
        List<FeedbackInfo> feedbacks,
        List<CategoryTodoInfo> categoryTodoCounts
) {
    public static DayStatisticInfo of(int totalMoveTime, int totalUsageTime, List<Feedback> feedbacks, Map<String, CategoryTodoStatistics.TimeCount> categoryTodoCount){

        return new DayStatisticInfo(
                totalMoveTime,
                totalUsageTime,
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