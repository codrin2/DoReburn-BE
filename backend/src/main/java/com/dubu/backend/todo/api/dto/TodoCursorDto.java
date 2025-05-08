package com.dubu.backend.todo.api.dto;

import com.dubu.backend.todo.core.dto.TodoCursor;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;

import java.util.LinkedHashMap;
import java.util.Map;

@Builder
public record TodoCursorDto(
        Long cursorCategoryId,
        String cursorDifficulty,
        Long cursorTodoId
) {
    @JsonValue
    public Object toJsonValue(){
        Map<String, Object> valueMap = new LinkedHashMap<>();
        if(cursorCategoryId != null) valueMap.put("cursorCategoryId", cursorCategoryId);
        if(cursorDifficulty != null) valueMap.put("cursorDifficulty", cursorDifficulty);
        if(cursorTodoId != null) valueMap.put("cursorTodoId", cursorTodoId);

        if(valueMap.size() == 1){
            return valueMap.values().iterator().next();
        }

        return valueMap;
    }

    public Boolean isEmpty(){
        return this.cursorCategoryId == null || this.cursorDifficulty == null || this.cursorTodoId == null;
    }

    public static TodoCursorDto from(TodoCursor cursor){
        if(cursor == null) return null;
        return TodoCursorDto.builder()
                .cursorCategoryId(cursor.categoryId())
                .cursorDifficulty(cursor.difficulty() != null ? cursor.difficulty().name() : null)
                .cursorTodoId(cursor.todoId())
                .build();
    }
}
