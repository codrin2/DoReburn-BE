package com.dubu.backend.todo.api.dto.response;

import com.dubu.backend.todo.application.dto.response.TodoItemResult;
import com.dubu.backend.todo.application.dto.response.TodoResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RecentTodoResponse(
        Long todoId,
        Long memberId,
        String title,
        String category,
        Boolean isAdded
){
    public static RecentTodoResponse from(TodoResult result){
        return RecentTodoResponse.builder()
                .todoId(result.todoId())
                .memberId(result.memberId())
                .category(result.category())
                .build();
    }

    public static RecentTodoResponse from(TodoItemResult result){
        return RecentTodoResponse.builder()
                .todoId(result.todoResult().todoId())
                .title(result.todoResult().title())
                .category(result.todoResult().category())
                .isAdded(result.hasChild())
                .build();
    }
}
