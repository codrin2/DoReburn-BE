package com.dubu.backend.todo.api.dto.response;

import com.dubu.backend.todo.application.dto.response.TodoResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record TodoResponse(
        Long todoId,
        String title,
        String category,
        String difficulty,
        String memo,
        @JsonInclude(JsonInclude.Include.NON_NULL) Boolean hasChild
) {
    public static TodoResponse from(TodoResult result){
        return TodoResponse.builder()
                .todoId(result.todoId())
                .hasChild(result.hasChild())
                .title(result.title())
                .category(result.category())
                .difficulty(result.difficulty())
                .memo(result.memo())
                .build();
    }
}
