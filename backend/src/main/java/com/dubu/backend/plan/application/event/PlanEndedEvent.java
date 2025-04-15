package com.dubu.backend.plan.application.event;

import com.dubu.backend.plan.domain.Plan;

public record PlanEndedEvent(Long memberId, Plan plan) {
}
