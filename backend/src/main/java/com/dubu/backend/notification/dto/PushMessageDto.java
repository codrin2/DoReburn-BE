package com.dubu.backend.notification.dto;

public record PushMessageDto(
        Long memberId,
        Long planId,
        String title,
        String body
) {
}