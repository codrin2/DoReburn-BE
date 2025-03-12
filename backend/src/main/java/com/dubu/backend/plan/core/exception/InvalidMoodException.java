package com.dubu.backend.plan.core.exception;

import com.dubu.backend.core.exception.BadRequestException;

import static com.dubu.backend.core.exception.ErrorCode.INVALID_MOOD;

public class InvalidMoodException extends BadRequestException {
    public InvalidMoodException(String mood) {
        super(INVALID_MOOD.getMessage().formatted(mood));
    }

    @Override
    public String getErrorCode() {
        return INVALID_MOOD.name();
    }
}
