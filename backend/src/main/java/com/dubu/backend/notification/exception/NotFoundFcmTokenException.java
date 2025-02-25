package com.dubu.backend.notification.exception;

import com.dubu.backend.global.exception.NotFoundException;

import static com.dubu.backend.global.exception.ErrorCode.NOT_FOUND_FCM_TOKEN;

public class NotFoundFcmTokenException extends NotFoundException {
    public NotFoundFcmTokenException(Long memberId) {
        super(NOT_FOUND_FCM_TOKEN.getMessage().formatted(memberId));
    }

    @Override
    public String getErrorCode() {
        return NOT_FOUND_FCM_TOKEN.name();
    }
}
