package com.dubu.backend.plan.api.request;

public record PlanFeedbackCreateRequest(
    String mood,
    String memo
) {
}