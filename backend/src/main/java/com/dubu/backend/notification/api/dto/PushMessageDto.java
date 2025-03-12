package com.dubu.backend.notification.api.dto;

public record PushMessageDto(
        Long memberId,
        Long planId,
        String title,
        String body
) {
}