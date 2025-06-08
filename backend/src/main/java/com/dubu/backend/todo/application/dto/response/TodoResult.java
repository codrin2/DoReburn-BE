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
        Long memberId,
        Long subPathId,
        Integer spentTime,
        Boolean isCompleted
) {
    public static TodoResult from(TodoInfo todoInfo) {
        return TodoResult.builder()
                .todoId(todoInfo.todoId())
                .title(todoInfo.title())
                .category(todoInfo.category().name())
                .difficulty(todoInfo.difficulty().name())
                .memo(todoInfo.memo())
                .build();
    }

    public static TodoResult from(Todo todo){
        return TodoResult.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .category(todo.getCategory().getName())
                .difficulty(todo.getDifficulty().name())
                .memo(todo.getMemo())
                .subPathId(todo.getSubPathId())
                .spentTime(todo.getSpentTime())
                .isCompleted(todo.getIsCompleted())
                .build();
    }

    public static TodoResult of(Long todoId, Long memberId, String category){
        return TodoResult.builder()
                .todoId(todoId)
                .memberId(memberId)
                .category(category)
                .build();
    }
}
