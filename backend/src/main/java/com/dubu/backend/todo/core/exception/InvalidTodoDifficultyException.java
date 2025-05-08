package com.dubu.backend.todo.core.exception;

import com.dubu.backend.core.exception.BadRequestException;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class InvalidTodoDifficultyException extends BadRequestException {
    public InvalidTodoDifficultyException(String difficulty) {
        super(INVALID_TODO_DIFFICULTY.getMessage().formatted(difficulty));
    }

    @Override
    public String getErrorCode() {
        return INVALID_TODO_DIFFICULTY.name();
    }
}
