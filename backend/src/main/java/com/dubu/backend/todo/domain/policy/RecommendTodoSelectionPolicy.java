package com.dubu.backend.todo.domain.policy;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RecommendTodoSelectionPolicy {
    private static final int TODAY_TODO_INIT_COUNT = 3;
    private static final int PERSONALIZED_RECOMMEND_COUNT = 5;

    public static List<Long> selectForTodayTodoInitialization(List<Long> todoIds){
        if(todoIds.size() < TODAY_TODO_INIT_COUNT) return todoIds;

        Collections.shuffle(todoIds);
        return todoIds.subList(0, TODAY_TODO_INIT_COUNT);
    }

    public static List<Long> selectForPersonalizedRecommend(List<Long> todoIds) {
        if(todoIds.size() < PERSONALIZED_RECOMMEND_COUNT) return todoIds;

        Collections.shuffle(todoIds);
        return todoIds.subList(0, PERSONALIZED_RECOMMEND_COUNT);
    }
}
