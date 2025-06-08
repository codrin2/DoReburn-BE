package com.dubu.backend.todo.api.dto.mapper;

import com.dubu.backend.todo.api.dto.response.TodoResponse;
import com.dubu.backend.todo.application.dto.response.TodoItemResult;
import com.dubu.backend.todo.application.dto.response.TodoResult;

import java.util.List;

public class TodoResponseMapper {
    public static List<TodoResponse> mapTodoResultToResponse(List<TodoResult> results) {
        return results.stream()
                .map(TodoResponse::from)
                .toList();
    }

    public static List<TodoResponse> mapTodoItemResultToResponse(List<TodoItemResult> results){
        return results.stream()
                .map(TodoResponse::from)
                .toList();
    }


}
