package com.dubu.backend.member.presentation;

public record MemberStatusChangeDto(
        Long memberId,
        Long planId
) {
}