package com.dubu.backend.notification.exception;

import com.dubu.backend.global.exception.ConflictException;

import static com.dubu.backend.global.exception.ErrorCode.DUPLICATE_SUBSCRIPTION;

public class DuplicateSubscriptionException extends ConflictException {
    public DuplicateSubscriptionException() {
        super(DUPLICATE_SUBSCRIPTION.getMessage());
    }

    @Override
    public String getErrorCode() {
        return DUPLICATE_SUBSCRIPTION.name();
    }
}