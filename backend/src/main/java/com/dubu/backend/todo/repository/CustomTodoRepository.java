package com.dubu.backend.todo.repository;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.share.dto.response.CategoryInfo;
import com.dubu.backend.todo.dto.common.Cursor;
import com.dubu.backend.todo.dto.search.TodoSearchCond;
import com.dubu.backend.todo.entity.Todo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CustomTodoRepository {
    Slice<Todo> findTodosUsingSingleCursor(Long cursor, TodoSearchCond cond, Pageable pageable);
    Slice<Todo> findTodosUsingCompositeCursor(Cursor cursor, TodoSearchCond cond, Pageable pageable);
    Map<String, Long> findTodoCountGroupByCategoryForStopMembers(List<Member> members, LocalDate date);
    Map<String, Long> findTodoCountGroupByCategoryForMoveOrFeedbackMembers(List<Member> members);
}
