package com.dubu.backend.core.domain.event;

public record PlanCreatedEvent(
        Long memberId,
        Long subPathId
) {
}
