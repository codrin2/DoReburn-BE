package com.dubu.backend.todo.application.dto.response;

import lombok.Builder;

@Builder
public record TodoItemResult(
        TodoResult todoResult,
        Boolean hasChild
){
    public static TodoItemResult of(TodoResult todoResult, Boolean hasChild){
        return TodoItemResult.builder()
                .todoResult(todoResult)
                .hasChild(hasChild)
                .build();
    }
}
