package com.dubu.backend.core.domain;

public record SuccessResponse<D>(
        D data
) {
    public static <D> SuccessResponse<D> of(D data){
        return new SuccessResponse<D>(data);
    }
}