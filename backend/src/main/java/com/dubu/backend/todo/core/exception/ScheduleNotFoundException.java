package com.dubu.backend.todo.core.exception;

import com.dubu.backend.core.exception.NotFoundException;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class ScheduleNotFoundException extends NotFoundException {

    public ScheduleNotFoundException() {
        super(SCHEDULE_NOT_FOUND.getMessage());
    }

    @Override
    public String getErrorCode() {
        return SCHEDULE_NOT_FOUND.name();
    }
}
