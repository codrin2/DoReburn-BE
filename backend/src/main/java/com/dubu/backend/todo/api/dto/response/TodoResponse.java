package com.dubu.backend.todo.api.dto.response;

import com.dubu.backend.todo.application.dto.response.TodoItemResult;
import com.dubu.backend.todo.application.dto.response.TodoResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodoResponse(
        Long todoId,
        String title,
        String category,
        String difficulty,
        String memo,
        Long memberId,
        Long subPathId,
        Integer spentTime,
        Boolean hasChild
) {
    public static TodoResponse from(TodoItemResult result){
        TodoResult todoResult = result.todoResult();

        return TodoResponse.builder()
                .todoId(todoResult.todoId())
                .hasChild(result.hasChild())
                .title(todoResult.title())
                .category(todoResult.category())
                .difficulty(todoResult.difficulty())
                .memo(todoResult.memo())
                .build();
    }

    public static TodoResponse from(TodoResult result){
        return TodoResponse.builder()
                .todoId(result.todoId())
                .title(result.title())
                .category(result.category())
                .difficulty(result.difficulty())
                .memo(result.memo())
                .memberId(result.memberId())
                .subPathId(result.subPathId())
                .spentTime(result.spentTime())
                .build();
    }
}
