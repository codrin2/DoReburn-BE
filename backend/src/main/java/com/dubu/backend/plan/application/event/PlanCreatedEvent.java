package com.dubu.backend.plan.application.event;

public record PlanCreatedEvent(
        Long memberId,
        Long subPathId
) {
}
