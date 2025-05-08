package com.dubu.backend.todo.core.exception;

import com.dubu.backend.core.exception.BadRequestException;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class InvalidTodoTypeException extends BadRequestException {
    public InvalidTodoTypeException(String type) {
        super(INVALID_TODO_TYPE.getMessage().formatted(type));
    }

    @Override
    public String getErrorCode() {
        return INVALID_TODO_TYPE.name();
    }
}
