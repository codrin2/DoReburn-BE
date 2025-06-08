package com.dubu.backend.share.application.dto;

import lombok.Builder;

@Builder
public record SurroundingMemberFetchCommand(
        Double x_coordinate,
        Double y_coordinate,
        Double radius
) {
}
