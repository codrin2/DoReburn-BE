package com.dubu.backend.todo.api.dto.response;

import com.dubu.backend.todo.application.dto.response.TodoResult;
import lombok.Builder;

@Builder
public record PathCompletedTodoResponse(
        Long todoId,
        String category,
        Long subPathId,
        Integer spentTime
) {
    public static PathCompletedTodoResponse from(TodoResult result) {
        return PathCompletedTodoResponse.builder()
                .todoId(result.todoId())
                .category(result.category())
                .subPathId(result.subPathId())
                .spentTime(result.spentTime())
                .build();
    }
}
