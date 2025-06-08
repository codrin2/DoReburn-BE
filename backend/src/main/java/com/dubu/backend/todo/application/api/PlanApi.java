package com.dubu.backend.todo.application.api;

import java.util.List;

public interface PlanApi {
    List<Long> getSubPathIdOfRecentPlans(List<Long> memberIds);
}
