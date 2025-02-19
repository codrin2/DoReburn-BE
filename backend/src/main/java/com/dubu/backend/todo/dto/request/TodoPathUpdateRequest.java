package com.dubu.backend.todo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "할 일 경로 수정 요청")
public record TodoPathUpdateRequest(
        @Schema(description = "새로운 경로 ID", example = "9999") Long newPathId) {
}
