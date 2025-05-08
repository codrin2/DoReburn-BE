package com.dubu.backend.todo.domain.dto;

import com.dubu.backend.todo.domain.Category;
import lombok.Builder;

@Builder
public record CategoryInfo(Long id, String name) {
    public static CategoryInfo from(Category category) {
        return CategoryInfo.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
