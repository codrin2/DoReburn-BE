package com.dubu.backend.todo.domain.enums;

import com.dubu.backend.todo.core.exception.InvalidTodoDifficultyException;

import java.util.Arrays;

public enum TodoType {
    SCHEDULED, FAVORITE, RECOMMEND, IN_PROGRESS, DONE;

    public static TodoType fromString(String value){
        return Arrays.stream(TodoType.values())
                .filter(t -> t.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidTodoDifficultyException(value));
    }
}