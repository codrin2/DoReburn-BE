package com.dubu.backend.todo.application.dto.response;

public record TomorrowTodoResult(
        boolean isTomorrowScheduleCreated,
        TodoResult todoResult
) {
    public static TomorrowTodoResult of(boolean isTomorrowScheduleCreated, TodoResult todoResult) {
        return new TomorrowTodoResult(isTomorrowScheduleCreated, todoResult);
    }
}
