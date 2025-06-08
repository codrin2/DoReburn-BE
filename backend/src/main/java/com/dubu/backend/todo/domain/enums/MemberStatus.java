package com.dubu.backend.todo.domain.enums;

import com.dubu.backend.todo.core.exception.InvalidMemberStatusException;

import java.util.Arrays;

public enum MemberStatus {
    ONBOARDING, STOP, MOVE, FEEDBACK;

    public static MemberStatus fromString(String value){
        return Arrays.stream(MemberStatus.values())
                .filter(ms -> ms.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidMemberStatusException(value));

    }
}
