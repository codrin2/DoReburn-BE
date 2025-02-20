package com.dubu.backend.share.dto.response;

public record MemberLocationInfo(Long memberId, Double x_coordinate, Double y_coordinate) {

    public static MemberLocationInfo of(Long memberId, Double x_coordinate, Double y_coordinate) {
        return new MemberLocationInfo(memberId, x_coordinate, y_coordinate);
    }
}