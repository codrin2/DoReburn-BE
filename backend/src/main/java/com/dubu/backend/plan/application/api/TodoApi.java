package com.dubu.backend.plan.application.api;

import com.dubu.backend.plan.domain.Todo;

import java.util.List;

public interface TodoApi {
    List<Todo> getTodos(List<Long> subPathIds);
    List<Todo> getCompletedTodos(List<Long> subPathIds);
}
