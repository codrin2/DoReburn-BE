package com.dubu.backend.todo.application.dto.request;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record TodoCompleteCommand(
        List<Long> planTodoIds,
        Map<Long, Integer> todoSpentTimeMap
) {
    public static TodoCompleteCommand of(List<Long> todoIds, Map<Long, Integer> todoSpentTimeMap){
        return TodoCompleteCommand.builder()
                .planTodoIds(todoIds)
                .todoSpentTimeMap(todoSpentTimeMap)
                .build();
    }
}
