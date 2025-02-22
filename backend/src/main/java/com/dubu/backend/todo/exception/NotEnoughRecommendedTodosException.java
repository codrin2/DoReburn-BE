package com.dubu.backend.todo.exception;

import com.dubu.backend.global.exception.BadRequestException;

import static com.dubu.backend.global.exception.ErrorCode.NOT_ENOUGH_RECOMMENDED_TODOS;

public class NotEnoughRecommendedTodosException extends BadRequestException {
    public NotEnoughRecommendedTodosException() {
        super(NOT_ENOUGH_RECOMMENDED_TODOS.getMessage());
    }

    @Override
    public String getErrorCode() {
        return NOT_ENOUGH_RECOMMENDED_TODOS.name();
    }
}