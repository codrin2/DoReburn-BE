package com.dubu.backend.plan.core.exception;

import com.dubu.backend.core.exception.BadRequestException;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class SubPathNotFoundException extends BadRequestException {
    public SubPathNotFoundException(Long subPathId) {
        super(PATH_NOT_FOUND.getMessage().formatted(subPathId));
    }

    @Override
    public String getErrorCode() {
        return PATH_NOT_FOUND.name();
    }
}
