package com.dubu.backend.share.application.api;

import com.dubu.backend.share.domain.RecentTodo;

import java.util.List;

public interface TodoApi {
    List<RecentTodo> getRecentTodosOfMembers(List<Long> memberIds);
    List<RecentTodo> getRecentTodos(Long memberId);
    void deleteFromFavorites(Long memberId, Long todoId);
}
