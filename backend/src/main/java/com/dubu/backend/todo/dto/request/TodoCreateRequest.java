package com.dubu.backend.todo.dto.request;

import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.todo.domain.*;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "할 일 직접 생성 요청")
public record TodoCreateRequest(
        @Schema(description = "할 일 제목", example = "종이 책 읽기") String title,
        @Schema(description = "할 일 카테고리", example = "READING") String category,
        @Schema(description = "할 일 난이도", example = "EASY") String difficulty,
        @Schema(description = "할 일 메모", example = "매일 30분 이상 독서") String memo){

    public Todo toEntity(Member member, Category category, Schedule schedule, SubPath subPath, TodoType type){
        return Todo.builder()
                .title(title)
                .category(category)
                .difficulty(TodoDifficulty.valueOf(difficulty))
                .memo(memo)
                .member(member)
                .schedule(schedule)
                .subPath(subPath)
                .type(type)
                .build();
    }
}
