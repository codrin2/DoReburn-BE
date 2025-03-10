package com.dubu.backend.todo.exception;

import com.dubu.backend.core.exception.NotFoundException;

import static com.dubu.backend.core.exception.ErrorCode.TODO_NOT_FOUND;

public class TodoNotFoundException extends NotFoundException {
    public TodoNotFoundException(Long todoId) {
        super(TODO_NOT_FOUND.getMessage().formatted(todoId));
    }

    @Override
    public String getErrorCode() {
        return TODO_NOT_FOUND.name();
    }
}
