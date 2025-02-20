package com.dubu.backend.member.dto;

public record MemberStatusChangeDto(
        Long memberId,
        Long planId
) {
}