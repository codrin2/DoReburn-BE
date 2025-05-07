package com.dubu.backend.todo.core.exception;

import com.dubu.backend.core.exception.BadRequestException;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class AlreadyAddedTodoFromArchiveException extends BadRequestException {
    public AlreadyAddedTodoFromArchiveException() {
        super(ALREADY_ADDED_TODO.getMessage());
    }

    @Override
    public String getErrorCode() {
        return ALREADY_ADDED_TODO.name();
    }
}
