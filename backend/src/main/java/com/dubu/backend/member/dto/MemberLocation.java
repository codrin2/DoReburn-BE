package com.dubu.backend.member.dto;

import jakarta.validation.constraints.NotNull;

public record MemberLocation(
        @NotNull(message = "x_coordinate 값은 필수입니다.")
        Double x_coordinate,
        @NotNull(message = "y_coordinate 값은 필수입니다.")
        Double y_coordinate
) {
}