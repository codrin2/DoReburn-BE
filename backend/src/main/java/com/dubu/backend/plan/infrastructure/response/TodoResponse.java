package com.dubu.backend.plan.infrastructure.response;

public record TodoResponse(
        Long todoId,
        String category,
        String title,
        String difficulty,
        String memo,
        Integer spentTime,
        Boolean isCompleted,
        Long subPathId
) {
}
