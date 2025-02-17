package com.dubu.backend.share.dto.response;

import com.dubu.backend.todo.entity.Category;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record CategoryInfo(String category, Long count) {
    public static CategoryInfo of(String category, Long count){
        return new CategoryInfo(category, count);
    }

    public static List<CategoryInfo> merge(List<Category> categories, Map<String, Long> todoCountOneByCategory, Map<String, Long> todoCountTwoByCategory){
        return categories.stream()
                .map(c -> new CategoryInfo(
                        c.getName(),
                        todoCountOneByCategory.getOrDefault(c.getName(), 0L)
                                + todoCountTwoByCategory.getOrDefault(c.getName(), 0L)
                ))
                .collect(Collectors.toList());
    }
}