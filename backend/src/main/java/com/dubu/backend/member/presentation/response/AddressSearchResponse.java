package com.dubu.backend.member.presentation.response;

public record AddressSearchResponse(
        String title,
        String roadAddress,
        Double x_coordinate,
        Double y_coordinate
) {
}