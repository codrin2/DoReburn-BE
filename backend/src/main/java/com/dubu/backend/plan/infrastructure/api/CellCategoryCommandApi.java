package com.dubu.backend.plan.infrastructure.api;

import com.dubu.backend.todo.domain.Todo;

import java.util.List;

public interface CellCategoryCommandApi {
    void updateCellCategory(Long memberId, List<Todo> beforeTodos, List<Todo> RecentTodos);
}
