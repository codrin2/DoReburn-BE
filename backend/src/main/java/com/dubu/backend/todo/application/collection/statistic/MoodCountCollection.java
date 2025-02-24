package com.dubu.backend.todo.application.collection.statistic;

import com.dubu.backend.plan.domain.enums.Mood;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MoodCountCollection {
    private final Map<Mood, Integer> moodCount;

    public MoodCountCollection() {
        moodCount = new HashMap<>();

        for (Mood mood : Mood.values()) {
            moodCount.put(mood, 0);
        }
    }

    public void addMood(Mood mood){
        moodCount.merge(mood, 1, Integer::sum);
    }
}
