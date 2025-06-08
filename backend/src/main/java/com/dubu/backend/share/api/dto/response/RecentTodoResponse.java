package com.dubu.backend.share.api.dto.response;

import com.dubu.backend.share.application.dto.RecentTodoResult;
import lombok.Builder;

@Builder
public record RecentTodoResponse(
    Long todoId,
    String title,
    String category,
    Boolean isSaved
) {
    public static RecentTodoResponse from(RecentTodoResult result){
        return RecentTodoResponse.builder()
                .todoId(result.todoId())
                .title(result.title())
                .category(result.category())
                .isSaved(result.isAdded())
                .build();
    }
}
