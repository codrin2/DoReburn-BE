package com.dubu.backend.todo.api.dto.request;

public record TodoCompletionToggleRequest(
        boolean isCompleted
) {
}
