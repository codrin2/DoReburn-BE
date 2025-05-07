package com.dubu.backend.todo.core.exception;

import com.dubu.backend.core.exception.BadRequestException;

import static com.dubu.backend.core.exception.ErrorCode.INVALID_TODO_REQUEST_TYPE;

public class InvalidTodoRequestTypeException extends BadRequestException {
    public InvalidTodoRequestTypeException(String type) {
        super(INVALID_TODO_REQUEST_TYPE.getMessage().formatted(type));
    }

    @Override
    public String getErrorCode() {
        return INVALID_TODO_REQUEST_TYPE.name();
    }
}
