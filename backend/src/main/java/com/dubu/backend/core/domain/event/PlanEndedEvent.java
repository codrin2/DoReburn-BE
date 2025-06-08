package com.dubu.backend.core.domain.event;

import java.util.List;

public record PlanEndedEvent(
        Long memberId,
        List<Long> todos,
        List<DoneTodo> doneTodos
//        Plan plan
) {
    public record DoneTodo(
            Long todoId,
            Integer spentTime
    ){}
}
