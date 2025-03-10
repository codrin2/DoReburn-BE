package com.dubu.backend.member.exception;

import com.dubu.backend.core.exception.UnauthorizedException;

import static com.dubu.backend.core.exception.ErrorCode.REFRESH_TOKEN_EXPIRED;

public class RefreshTokenExpiredException extends UnauthorizedException {
    public RefreshTokenExpiredException() {
        super(REFRESH_TOKEN_EXPIRED.getMessage());
    }

    @Override
    public String getErrorCode() {
        return REFRESH_TOKEN_EXPIRED.name();
    }
}