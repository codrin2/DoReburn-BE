package com.dubu.backend.member.core.exception;

import com.dubu.backend.core.exception.UnauthorizedException;

import static com.dubu.backend.core.exception.ErrorCode.TOKEN_INVALID;

public class TokenInvalidException extends UnauthorizedException {

    public TokenInvalidException() {
        super(TOKEN_INVALID.getMessage());
    }

    @Override
    public String getErrorCode() {
        return TOKEN_INVALID.name();
    }
}