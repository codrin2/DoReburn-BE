package com.dubu.backend.share.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecentTodo {
    private Long todoId;
    private Long memberId;
    private String title;
    private String category;
    private Boolean isAdded;

    public static RecentTodo of(Long todoId, Long memberId, String title, String category, Boolean isAdded){
        return RecentTodo.builder()
                .todoId(todoId)
                .memberId(memberId)
                .title(title)
                .category(category)
                .isAdded(isAdded)
                .build();
    }
}
