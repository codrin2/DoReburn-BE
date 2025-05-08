package com.dubu.backend.todo.api.dto.request;

import com.dubu.backend.todo.core.exception.InvalidEditPageTypeException;

import java.util.Arrays;

public enum EditPageType {
    TODAY, TOMORROW, PATH, FAVORITE;

    public static EditPageType fromString(String value){
        return Arrays.stream(EditPageType.values())
                .filter(t -> t.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidEditPageTypeException(value));
    }
}
