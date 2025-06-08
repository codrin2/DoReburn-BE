package com.dubu.backend.member.domain.enums;

import com.dubu.backend.member.core.exception.InvalidStatusException;

import java.util.Arrays;

public enum MemberStatus {
    ONBOARDING, STOP, MOVE, FEEDBACK;

    public static MemberStatus fromString(String value) {
        return Arrays.stream(MemberStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidStatusException(value));
    }
}