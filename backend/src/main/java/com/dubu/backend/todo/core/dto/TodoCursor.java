package com.dubu.backend.todo.core.dto;


import com.dubu.backend.todo.api.dto.TodoCursorDto;
import com.dubu.backend.todo.api.dto.request.TodoRequestType;
import com.dubu.backend.todo.domain.dto.TodoInfo;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import lombok.Builder;

@Builder
public record TodoCursor(
        Long todoId,
        Long categoryId,
        TodoDifficulty difficulty) {

    public static TodoCursor empty(){
        return TodoCursor.builder()
                .build();
    }
    public static TodoCursor of(Long todoId){
        return TodoCursor.builder()
                .todoId(todoId)
                .build();
    }

    public static TodoCursor from(TodoCursorDto dto){
        if(dto == null || dto.isEmpty()) return null;

        return TodoCursor.builder()
                .categoryId(dto.cursorCategoryId())
                .difficulty(TodoDifficulty.fromString(dto.cursorDifficulty()))
                .todoId(dto.cursorTodoId())
                .build();
    }


    public static TodoCursor from(TodoRequestType requestType, TodoInfo todoInfo) {
        return switch(requestType){
            case FAVORITE -> TodoCursor.builder()
                    .todoId(todoInfo.todoId()).build();
            case RECOMMEND -> TodoCursor.builder()
                    .todoId(todoInfo.todoId())
                    .categoryId(todoInfo.category().id())
                    .difficulty(todoInfo.difficulty())
                    .build();
            default -> null;
        };
    }
}
