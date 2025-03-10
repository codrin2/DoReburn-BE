package com.dubu.backend.notification.core.exception;

import com.dubu.backend.core.exception.ConflictException;

import static com.dubu.backend.core.exception.ErrorCode.DUPLICATE_SUBSCRIPTION;

public class DuplicateSubscriptionException extends ConflictException {
    public DuplicateSubscriptionException() {
        super(DUPLICATE_SUBSCRIPTION.getMessage());
    }

    @Override
    public String getErrorCode() {
        return DUPLICATE_SUBSCRIPTION.name();
    }
}