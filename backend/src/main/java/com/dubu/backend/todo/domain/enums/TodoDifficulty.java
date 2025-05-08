package com.dubu.backend.todo.domain.enums;


import com.dubu.backend.todo.core.exception.InvalidTodoTypeException;

import java.util.Arrays;

public enum TodoDifficulty {
    EASY, NORMAL, HARD;

    public static TodoDifficulty fromString(String value){
        return Arrays.stream(TodoDifficulty.values())
                .filter(t -> t.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidTodoTypeException(value));
    }
}