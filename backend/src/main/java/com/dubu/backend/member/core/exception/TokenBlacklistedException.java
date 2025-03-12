package com.dubu.backend.member.core.exception;

import com.dubu.backend.core.exception.UnauthorizedException;

import static com.dubu.backend.core.exception.ErrorCode.TOKEN_BLACKLISTED;

public class TokenBlacklistedException extends UnauthorizedException {

    public TokenBlacklistedException() {
        super(TOKEN_BLACKLISTED.getMessage());
    }

    @Override
    public String getErrorCode() {
        return TOKEN_BLACKLISTED.name();
    }
}