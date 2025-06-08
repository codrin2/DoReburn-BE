package com.dubu.backend.share.application.dto;

import com.dubu.backend.share.domain.RecentTodo;
import lombok.Builder;

@Builder
public record RecentTodoResult(
        Long todoId,
        String title,
        String category,
        Boolean isAdded
) {
    public static RecentTodoResult from(RecentTodo recentTodo){
        return RecentTodoResult.builder()
                .todoId(recentTodo.getTodoId())
                .title(recentTodo.getTitle())
                .category(recentTodo.getCategory())
                .isAdded(recentTodo.getIsAdded())
                .build();
    }
}
