package com.dubu.backend.share.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurroundingMember {
    private Long memberId;
    private String nickname;
    private Double x_coordinate;
    private Double y_coordinate;
    private List<String> recentTodoCategories;

    public static SurroundingMember of(Long memberId, Double longitude, Double latitude){
        return SurroundingMember.builder()
                .memberId(memberId)
                .x_coordinate(longitude)
                .y_coordinate(latitude)
                .build();
    }

    public static SurroundingMember of(Long memberId, String nickname){
        return SurroundingMember.builder()
                .memberId(memberId)
                .nickname(nickname)
                .build();
    }

    public void populateRecentTodoCategories(List<String> recentTodoCategories){
        this.recentTodoCategories = List.copyOf(recentTodoCategories);
    }
}
