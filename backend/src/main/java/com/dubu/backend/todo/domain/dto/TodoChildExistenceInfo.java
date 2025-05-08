package com.dubu.backend.todo.domain.dto;

public record TodoChildExistenceInfo(
        Long todoId,
        Boolean hasChild
) { }
