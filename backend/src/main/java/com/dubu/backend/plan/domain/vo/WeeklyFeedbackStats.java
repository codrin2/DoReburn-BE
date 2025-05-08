package com.dubu.backend.plan.domain.vo;

import com.dubu.backend.plan.domain.enums.Mood;
import lombok.Builder;

import java.util.Map;

@Builder
public record WeeklyFeedbackStats(Map<Mood, Integer> moodCountMap) {
    public static WeeklyFeedbackStats of(Map<Mood, Integer> moodCountMap){
        return new WeeklyFeedbackStats(Map.copyOf(moodCountMap));
    }
}
