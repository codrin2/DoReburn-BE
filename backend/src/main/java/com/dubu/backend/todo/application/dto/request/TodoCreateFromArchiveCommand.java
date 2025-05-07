package com.dubu.backend.todo.application.dto.request;

import lombok.Builder;

@Builder
public record TodoCreateFromArchiveCommand(
        Long subPathId,
        Long archivedTodoId
) {
}
