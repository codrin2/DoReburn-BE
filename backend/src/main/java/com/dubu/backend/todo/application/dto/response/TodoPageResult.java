package com.dubu.backend.todo.application.dto.response;

import com.dubu.backend.todo.core.dto.TodoCursor;

import java.util.List;

public record TodoPageResult(
        Boolean hasNext,
        TodoCursor cursor,
        List<TodoResult> todoResults
) {
    public static TodoPageResult of(Boolean hasNext, TodoCursor cursor, List<TodoResult> todoResults) {

        return new TodoPageResult(hasNext, cursor, todoResults);
    }
}
