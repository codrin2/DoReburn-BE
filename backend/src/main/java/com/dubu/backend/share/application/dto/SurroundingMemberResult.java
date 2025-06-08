package com.dubu.backend.share.application.dto;

import com.dubu.backend.share.domain.RecentTodo;
import com.dubu.backend.share.domain.SurroundingMember;
import lombok.Builder;

import java.util.List;

@Builder
public record SurroundingMemberResult(
        String nickname,
        List<RecentTodoResult> todos
) {
    public static SurroundingMemberResult from(SurroundingMember member, List<RecentTodo> recentTodos
    ){
        return SurroundingMemberResult.builder()
                .nickname(member.getNickname())
                .todos(
                        recentTodos.stream()
                                .map(RecentTodoResult::from)
                                .toList()
                ).build();
    }
}
