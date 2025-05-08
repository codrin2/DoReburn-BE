package com.dubu.backend.todo.application.dto.request;

import com.dubu.backend.todo.core.dto.TodoCursor;
import lombok.Builder;

import java.util.List;

@Builder
public record TodoFetchPagedCommand(
        TodoCursor cursor,
        Integer size,
        Long subPathId,
        List<String> categories,
        List<String> difficulties
) {
    public static TodoFetchPagedCommand of(TodoCursor cursor, Long subPathId, int size){
        return TodoFetchPagedCommand.builder()
                .cursor(cursor)
                .subPathId(subPathId)
                .size(size)
                .build();
    }

    public static TodoFetchPagedCommand of(TodoCursor cursor, Long subPathId, List<String> categories, List<String> difficulties, int size){
        return TodoFetchPagedCommand.builder()
                .cursor(cursor)
                .subPathId(subPathId)
                .categories(categories != null ? List.copyOf(categories): null)
                .difficulties(difficulties != null ? List.copyOf(difficulties): null)
                .size(size)
                .build();
    }
}
