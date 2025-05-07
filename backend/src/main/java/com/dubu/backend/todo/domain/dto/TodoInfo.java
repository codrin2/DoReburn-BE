package com.dubu.backend.todo.domain.dto;

import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import lombok.Builder;

@Builder
public record TodoInfo(
        Long todoId,
        String title,
        CategoryInfo category,
        TodoDifficulty difficulty,
        String memo
) {
    public static TodoInfo from(Todo todo){
        return TodoInfo.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .category(CategoryInfo.from(todo.getCategory()))
                .difficulty(todo.getDifficulty())
                .memo(todo.getMemo())
                .build();
    }
}
