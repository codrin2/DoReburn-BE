package com.dubu.backend.todo.exception;

import com.dubu.backend.core.exception.BadRequestException;
import com.dubu.backend.todo.domain.enums.TodoType;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class TodoTypeMismatchException extends BadRequestException {
    public TodoTypeMismatchException(TodoType todoType, TodoType requestType) {
        super(TODO_TYPE_MISMATCH.getMessage().formatted(todoType.name(), requestType.name()));
    }

    @Override
    public String getErrorCode() {
        return TODO_TYPE_MISMATCH.name();
    }
}
