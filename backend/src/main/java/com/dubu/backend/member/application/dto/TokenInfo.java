package com.dubu.backend.member.application.dto;

import java.time.Instant;

public record TokenInfo(
        String tokenId,
        String memberId,
        Instant expiration
) {
}
