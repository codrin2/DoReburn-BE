package com.dubu.backend.share.api.dto.response;

import com.dubu.backend.share.application.dto.SurroundingMemberResult;
import lombok.Builder;

import java.util.List;

@Builder
public record SurroundingMemberResponse(
        String nickname,
        List<RecentTodoResponse> todos
) {
    public static SurroundingMemberResponse from(SurroundingMemberResult result){
        return SurroundingMemberResponse.builder()
                .nickname(result.nickname())
                .todos(result.todos().stream()
                        .map(RecentTodoResponse::from)
                        .toList()
                ).build();
    }
}
