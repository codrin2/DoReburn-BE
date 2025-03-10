package com.dubu.backend.notification.application.response;

public record MemberResponse(
        Long memberId,
        String status,
        String recentRoute
) {
}