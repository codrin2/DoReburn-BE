package com.dubu.backend.member.api.response;

public record AddressSearchResponse(
        String title,
        String roadAddress,
        Double x_coordinate,
        Double y_coordinate
) {
}