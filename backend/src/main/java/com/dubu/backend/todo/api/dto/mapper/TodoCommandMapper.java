package com.dubu.backend.todo.api.dto.mapper;

import com.dubu.backend.todo.api.dto.request.TodoCreateFromArchiveRequest;
import com.dubu.backend.todo.api.dto.request.TodoCreateRequest;
import com.dubu.backend.todo.api.dto.request.TodoUpdateRequest;
import com.dubu.backend.todo.application.dto.request.TodoCreateCommand;
import com.dubu.backend.todo.application.dto.request.TodoCreateFromArchiveCommand;
import com.dubu.backend.todo.application.dto.request.TodoUpdateCommand;
import org.springframework.stereotype.Component;

@Component
public class TodoCommandMapper {
    public static TodoCreateCommand toCommand(Long subPathId, TodoCreateRequest request){
        return TodoCreateCommand.builder()
                .subPathId(subPathId)
                .title(request.title())
                .category(request.category())
                .difficulty(request.difficulty())
                .memo(request.memo())
                .build();
    }

    public static TodoCreateFromArchiveCommand toCommand(Long subPathId, TodoCreateFromArchiveRequest request){
        return TodoCreateFromArchiveCommand.builder()
                .subPathId(subPathId)
                .archivedTodoId(request.todoId())
                .build();
    }

    public static TodoUpdateCommand toCommand(Long todoId, TodoUpdateRequest request) {
        return TodoUpdateCommand.builder()
                .todoId(todoId)
                .title(request.title())
                .category(request.category())
                .difficulty(request.difficulty())
                .memo(request.memo())
                .build();
    }
}
