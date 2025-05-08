package com.dubu.backend.todo.domain.repository.dto;

import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import lombok.Builder;

import java.util.List;

@Builder
public record TodoSearchCond(
        Long memberId,
        TodoType type,
        List<Category> categories,
        List<TodoDifficulty> difficulties
){
    public static TodoSearchCond of(TodoType type, List<Category> categories){
        return TodoSearchCond.builder()
                .type(type)
                .categories(categories)
                .build();
    }

    public static TodoSearchCond of(TodoType type, List<Category> categories, List<TodoDifficulty> difficulties){
        return TodoSearchCond.builder()
                .type(type)
                .categories(categories)
                .difficulties(difficulties)
                .build();
    }

    public static TodoSearchCond of(Long memberId, TodoType type){
        return TodoSearchCond.builder()
                .memberId(memberId)
                .type(type)
                .build();
    }
}
