package com.dubu.backend.statistic.dto.response;

import com.dubu.backend.statistic.service.collection.CategoryTodoStatistics;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Map;

public record CategoryTodoInfo(String category, @JsonInclude(JsonInclude.Include.NON_NULL) @Nullable Integer usageTime, int count) implements Comparable<CategoryTodoInfo>{
    @Override
    public int compareTo(CategoryTodoInfo other) {
        if (this.usageTime != null && other.usageTime != null) {
            int cmp = other.usageTime.compareTo(this.usageTime);
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(other.count, this.count);
    }

    public static List<CategoryTodoInfo> fromCategoryTodoTimeCount(Map<String, CategoryTodoStatistics.TimeCount> categoryTodoCount){
        return categoryTodoCount.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getCount() > 0)
                .map(entry -> {
                    if(entry.getValue().getTime() == 0){
                        return new CategoryTodoInfo(entry.getKey(), null, entry.getValue().getCount());
                    }
                    return new CategoryTodoInfo(entry.getKey(), entry.getValue().getTime(), entry.getValue().getCount());
                })
                .sorted()
                .toList();
    }
}