package com.dubu.backend.todo.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "할 일 직접 생성 요청")
public record TodoCreateRequest(
        @Schema(description = "할 일 제목", example = "종이 책 읽기") String title,
        @Schema(description = "할 일 카테고리", example = "READING") String category,
        @Schema(description = "할 일 난이도", example = "EASY") String difficulty,
        @Schema(description = "할 일 메모", example = "매일 30분 이상 독서") String memo) {}

