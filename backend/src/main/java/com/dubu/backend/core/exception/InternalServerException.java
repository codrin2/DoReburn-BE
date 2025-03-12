package com.dubu.backend.core.exception;

public abstract class InternalServerException extends RuntimeException {
  public InternalServerException(String message) {
    super(message);
  }

  public abstract String getErrorCode();
}