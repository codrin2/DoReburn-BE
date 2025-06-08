package com.dubu.backend.share.infrastructure.dto;

public record TodoResponse(
        Long todoId,
        Long memberId,
        String category,
        String title,
        Boolean isAdded
) {
}
