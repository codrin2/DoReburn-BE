package com.dubu.backend.todo.dto.search;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import lombok.Builder;

import java.util.List;

@Builder
public record TodoSearchCond(Member member, TodoType type, List<Category> categories, List<TodoDifficulty> difficulties) {
}
