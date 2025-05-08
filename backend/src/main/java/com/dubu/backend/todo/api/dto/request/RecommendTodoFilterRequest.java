package com.dubu.backend.todo.api.dto.request;

import jakarta.annotation.Nullable;

import java.util.List;

public record RecommendTodoFilterRequest(
        @Nullable List<String> categories,
        @Nullable  List<String> difficulties
) {
}
