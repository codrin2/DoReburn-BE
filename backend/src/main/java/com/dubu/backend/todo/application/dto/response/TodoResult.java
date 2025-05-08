package com.dubu.backend.todo.application.dto.response;

import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.dto.TodoInfo;
import lombok.Builder;

@Builder
public record TodoResult(
        Long todoId,
        String title,
        String category,
        String difficulty,
        String memo,
        Boolean hasChild
) {
    public static TodoResult from(TodoInfo todoInfo) {
        return TodoResult.builder()
                .todoId(todoInfo.todoId())
                .title(todoInfo.title())
                .category(todoInfo.category().name())
                .difficulty(todoInfo.difficulty().name())
                .memo(todoInfo.memo())
                .hasChild(false)
                .build();
    }

    public static TodoResult from(TodoInfo todoInfo, boolean hasChild) {
        return TodoResult.builder()
                .todoId(todoInfo.todoId())
                .title(todoInfo.title())
                .category(todoInfo.category().name())
                .difficulty(todoInfo.difficulty().name())
                .memo(todoInfo.memo())
                .hasChild(hasChild)
                .build();
    }

    public static TodoResult from(Todo todo){
        return TodoResult.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .category(todo.getCategory().getName())
                .difficulty(todo.getDifficulty().name())
                .memo(todo.getMemo())
                .build();
    }

    public static TodoResult from(Todo todo, boolean hasChild) {
        return TodoResult.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .category(todo.getCategory().getName())
                .difficulty(todo.getDifficulty().name())
                .memo(todo.getMemo())
                .hasChild(hasChild)
                .build();
    }
}
