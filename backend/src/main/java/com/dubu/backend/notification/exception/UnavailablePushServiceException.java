package com.dubu.backend.notification.exception;

import com.dubu.backend.global.exception.ServiceUnavailableException;

import static com.dubu.backend.global.exception.ErrorCode.UNAVAILABLE_PUSH_SERVICE;

public class UnavailablePushServiceException extends ServiceUnavailableException {
    public UnavailablePushServiceException() {
        super(UNAVAILABLE_PUSH_SERVICE.getMessage());
    }

    @Override
    public String getErrorCode() {
        return UNAVAILABLE_PUSH_SERVICE.name();
    }
}