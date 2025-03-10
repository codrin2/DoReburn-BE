package com.dubu.backend.core.domain;

public record SuccessResponse<D>(
        D data
) {
}