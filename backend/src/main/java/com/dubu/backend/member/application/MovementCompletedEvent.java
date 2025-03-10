package com.dubu.backend.member.application;

public record MovementCompletedEvent(
        Long memberId,
        Long planId
) {
}