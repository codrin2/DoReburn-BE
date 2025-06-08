package com.dubu.backend.todo.application.dto.response;

import com.dubu.backend.todo.core.dto.TodoCursor;

import java.util.List;

public record TodoPageResult(
        Boolean hasNext,
        TodoCursor cursor,
        List<TodoItemResult> todoItemResults
) {
    public static TodoPageResult of(Boolean hasNext, TodoCursor cursor, List<TodoItemResult> todoItemResults) {

        return new TodoPageResult(hasNext, cursor, todoItemResults);
    }
}
