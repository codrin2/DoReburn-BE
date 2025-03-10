package com.dubu.backend.member.application.event;

public record MovementCompletedEvent(
        Long memberId,
        Long planId
) {
}