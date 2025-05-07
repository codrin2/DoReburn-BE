package com.dubu.backend.todo.core.exception;

import com.dubu.backend.core.exception.BadRequestException;
import com.dubu.backend.todo.api.dto.request.TodoRequestType;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class UnsupportedOperationForTodoRequestTypeException extends BadRequestException {
    public UnsupportedOperationForTodoRequestTypeException(TodoRequestType requestType) {
        super(UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION.getMessage().formatted(requestType.name()));
    }

    @Override
    public String getErrorCode() {
        return UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION.name();
    }
}
