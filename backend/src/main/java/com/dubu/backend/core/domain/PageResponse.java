package com.dubu.backend.core.domain;

public record PageResponse<C, T>(boolean hasNext, C nextCursor, T data) {
}
