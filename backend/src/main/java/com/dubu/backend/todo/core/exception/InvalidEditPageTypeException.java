package com.dubu.backend.todo.core.exception;

import com.dubu.backend.core.exception.BadRequestException;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class InvalidEditPageTypeException extends BadRequestException {
    public InvalidEditPageTypeException(String type) {
        super(INVALID_MODIFY_PAGE_TYPE.getMessage().formatted(type));
    }

    @Override
    public String getErrorCode() {
        return INVALID_MODIFY_PAGE_TYPE.name();
    }
}
