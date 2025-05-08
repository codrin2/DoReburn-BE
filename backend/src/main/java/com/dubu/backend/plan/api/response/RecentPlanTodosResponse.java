package com.dubu.backend.plan.api.response;

import com.dubu.backend.plan.domain.Todo;

import java.util.List;

public record RecentPlanTodosResponse(List<Todo> todos) {

    public static RecentPlanTodosResponse from(List<Todo> todos){
        return new RecentPlanTodosResponse(todos);
    }
}
