package com.dubu.backend.todo.api.dto.response;

import com.dubu.backend.todo.application.dto.response.TodoResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PathTodoResponse(
        Long todoId,
        String title,
        String category,
        String difficulty,
        String memo,
        Integer spentTime,
        Boolean isCompleted,
        Long subPathId
) {
    public static PathTodoResponse from(TodoResult result) {
        return PathTodoResponse.builder()
                .todoId(result.todoId())
                .title(result.title())
                .category(result.category())
                .difficulty(result.difficulty())
                .memo(result.memo())
                .spentTime(result.spentTime())
                .isCompleted(result.isCompleted())
                .subPathId(result.subPathId())
                .build();
    }
}
