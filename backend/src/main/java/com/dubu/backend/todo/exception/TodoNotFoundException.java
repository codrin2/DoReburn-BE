package com.dubu.backend.todo.exception;

import com.dubu.backend.core.exception.NotFoundException;

import static com.dubu.backend.core.exception.ErrorCode.TODO_NOT_FOUND;
import static com.dubu.backend.core.exception.ErrorCode.TODO_NOT_FOUND_WITH_ID;

public class TodoNotFoundException extends NotFoundException {
    public TodoNotFoundException() {super(TODO_NOT_FOUND.getMessage());}

    public TodoNotFoundException(Long todoId) {
        super(TODO_NOT_FOUND_WITH_ID.getMessage().formatted(todoId));
    }

    @Override
    public String getErrorCode() {
        return TODO_NOT_FOUND_WITH_ID.name();
    }
}
