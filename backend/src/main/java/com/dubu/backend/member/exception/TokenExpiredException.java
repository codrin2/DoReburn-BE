package com.dubu.backend.member.exception;

import com.dubu.backend.core.exception.UnauthorizedException;

import static com.dubu.backend.core.exception.ErrorCode.TOKEN_EXPIRED;

public class TokenExpiredException extends UnauthorizedException {

    public TokenExpiredException() {
        super(TOKEN_EXPIRED.getMessage());
    }

    @Override
    public String getErrorCode() {
        return TOKEN_EXPIRED.name();
    }
}