package com.dubu.backend.todo.application.dto.request;

import lombok.Builder;

@Builder
public record TodoCreateCommand(
        Long subPathId,
        String title,
        String category,
        String difficulty,
        String memo
        ) {

        public static TodoCreateCommand of(String title, String category, String difficulty, String memo, Long subPathId){
                return TodoCreateCommand.builder()
                        .title(title)
                        .category(category)
                        .difficulty(difficulty)
                        .memo(memo)
                        .subPathId(subPathId)
                        .build();
        }
}
