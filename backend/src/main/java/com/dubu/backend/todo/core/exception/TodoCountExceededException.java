package com.dubu.backend.todo.core.exception;

import com.dubu.backend.core.exception.BadRequestException;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class TodoCountExceededException extends BadRequestException {
    public TodoCountExceededException(String type, int count) {
        super(TODO_COUNT_EXCEEDED.getMessage().formatted(type, count));
    }

    @Override
    public String getErrorCode() {
        return TODO_COUNT_EXCEEDED.name();
    }
}
