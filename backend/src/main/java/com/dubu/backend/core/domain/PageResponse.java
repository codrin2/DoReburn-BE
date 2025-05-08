package com.dubu.backend.core.domain;

import lombok.Builder;

@Builder
public record PageResponse<C, T>(
        boolean hasNext,
        C nextCursor,
        T data) {

    public static <C, T> PageResponse<C, T> of(boolean hasNext, C nextCursor, T data) {
        return PageResponse.<C, T>builder()
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .data(data)
                .build();
    }
}
