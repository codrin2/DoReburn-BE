package com.dubu.backend.plan.infrastructure.response;

import java.time.LocalDate;

public record MemberResponse(
        Long memberId,
        String status,
        LocalDate createdAt
) {
}
