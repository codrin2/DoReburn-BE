package com.dubu.backend.todo.exception;


import com.dubu.backend.global.exception.ErrorCode;
import com.dubu.backend.global.exception.NotFoundException;

import static com.dubu.backend.global.exception.ErrorCode.*;

public class SaveTodoNotFoundFromTargetParentException extends NotFoundException {
    public SaveTodoNotFoundFromTargetParentException(Long parentId) {
        super(SAVE_TODO_NOT_FOUND_FROM_TARGET_PARENT.getMessage().formatted(parentId));
    }

    @Override
    public String getErrorCode() {
        return SAVE_TODO_NOT_FOUND_FROM_TARGET_PARENT.name();
    }
}
