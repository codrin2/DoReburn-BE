package com.dubu.backend.todo.dto.response;

import java.util.List;

public record SurroundingMemberLocationInfo(Long memberId, Double x_coordinate, Double y_coordinate, List<String> category) {

    public static SurroundingMemberLocationInfo of(Long memberId, Double x_coordinate, Double y_coordinate, List<String> category) {
        return new SurroundingMemberLocationInfo(memberId, x_coordinate, y_coordinate, category);
    }
}
