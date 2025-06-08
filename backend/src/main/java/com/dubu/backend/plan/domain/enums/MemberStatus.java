package com.dubu.backend.plan.domain.enums;

import com.dubu.backend.plan.core.exception.InvalidMemberStatusException;

import java.util.Arrays;

public enum MemberStatus {
    ONBOARDING, STOP, MOVE, FEEDBACK;

    public static MemberStatus fromString(String value) {
        return Arrays.stream(MemberStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidMemberStatusException(value));
    }
}
