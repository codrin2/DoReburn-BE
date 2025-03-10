package com.dubu.backend.member.exception;

import com.dubu.backend.core.exception.UnauthorizedException;

import static com.dubu.backend.core.exception.ErrorCode.MISSING_TOKEN_IN_COOKIE;

public class MissingTokenInCookieException extends UnauthorizedException {
    public MissingTokenInCookieException() {
        super(MISSING_TOKEN_IN_COOKIE.getMessage());
    }

    @Override
    public String getErrorCode() {
        return MISSING_TOKEN_IN_COOKIE.name();
    }
}
