package com.dubu.backend.plan.exception;

import com.dubu.backend.global.exception.NotFoundException;

import static com.dubu.backend.global.exception.ErrorCode.NOT_FOUND_PLAN;

public class PlanNotFoundException extends NotFoundException {
    public PlanNotFoundException() {
        super(NOT_FOUND_PLAN.getMessage().formatted(0));
    }

    public PlanNotFoundException(Long planId) {
        super(NOT_FOUND_PLAN.getMessage().formatted(planId));
    }

    @Override
    public String getErrorCode() {
      return NOT_FOUND_PLAN.name();
    }
}