package com.dubu.backend.member.exception;

import com.dubu.backend.global.exception.InternalServerException;

import static com.dubu.backend.global.exception.ErrorCode.REDIS_UNAVAILABLE;

public class RedisUnavailableException extends InternalServerException {
    public RedisUnavailableException() {
        super(REDIS_UNAVAILABLE.getMessage());
    }

    @Override
    public String getErrorCode() {
        return REDIS_UNAVAILABLE.name();
    }
}