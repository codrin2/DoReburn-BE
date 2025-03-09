package com.dubu.backend.member.presentation;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record MemberLocationDto(
        @NotNull(message = "x_coordinate 값은 필수입니다.")
        @DecimalMin(value = "-180.0", message = "x_coordinate 값은 -180보다 커야 합니다.")
        @DecimalMax(value = "180.0", message = "x_coordinate 값은 180보다 작아야 합니다.")
        Double x_coordinate,
        @NotNull(message = "y_coordinate 값은 필수입니다.")
        @DecimalMin(value = "-90.0", message = "y_coordinate 값은 -90보다 커야 합니다.")
        @DecimalMax(value = "90.0", message = "y_coordinate 값은 90보다 작아야 합니다.")
        Double y_coordinate
) {
}