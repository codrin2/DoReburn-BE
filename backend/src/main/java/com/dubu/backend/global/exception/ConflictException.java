package com.dubu.backend.global.exception;

public abstract class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public abstract String getErrorCode();
}