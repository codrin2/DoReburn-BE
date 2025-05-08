package com.dubu.backend.todo.api.dto.request;

import com.dubu.backend.todo.core.exception.InvalidTodoRequestTypeException;

import java.util.Arrays;

public enum TodoRequestType {
    TODAY, TOMORROW, PATH, FAVORITE, RECOMMEND;

    public static TodoRequestType fromString(String value){
        return Arrays.stream(TodoRequestType.values())
                .filter(t -> t.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidTodoRequestTypeException(value));
    }
}
