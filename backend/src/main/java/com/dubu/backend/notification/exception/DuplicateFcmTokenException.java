package com.dubu.backend.notification.exception;

import com.dubu.backend.global.exception.BadRequestException;

import static com.dubu.backend.global.exception.ErrorCode.DUPLICATE_FCM_TOKEN;

public class DuplicateFcmTokenException extends BadRequestException {
    public DuplicateFcmTokenException(Long memberId) {
        super(DUPLICATE_FCM_TOKEN.getMessage().formatted(memberId));
    }

    @Override
    public String getErrorCode() {
      return DUPLICATE_FCM_TOKEN.name();
    }
}
