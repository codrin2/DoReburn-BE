package com.dubu.backend.share.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryRank {
    private String category;
    private Integer count;

    public static CategoryRank of(String category, Integer count){
        return CategoryRank.builder()
                .category(category)
                .count(count)
                .build();
    }
}
