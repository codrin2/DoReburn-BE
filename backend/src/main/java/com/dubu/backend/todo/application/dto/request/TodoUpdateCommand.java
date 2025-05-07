package com.dubu.backend.todo.application.dto.request;

import lombok.Builder;

@Builder
public record TodoUpdateCommand(
        Long todoId,
        String title,
        String category,
        String difficulty,
        String memo
) {
}
