package com.dubu.backend.plan.core.exception;

import com.dubu.backend.core.exception.NotFoundException;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class PathNotFoundException extends NotFoundException {
    public PathNotFoundException(Long pathId) {
        super(PATH_NOT_FOUND.getMessage().formatted(pathId));
    }

    @Override
    public String getErrorCode() {
        return PATH_NOT_FOUND.name();
    }
}
