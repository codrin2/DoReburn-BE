package com.dubu.backend.todo.domain.repository;

import com.dubu.backend.todo.core.dto.TodoCursor;
import com.dubu.backend.todo.domain.dto.TodoChildExistenceInfo;
import com.dubu.backend.todo.domain.dto.TodoInfo;
import com.dubu.backend.todo.domain.dto.TodoChildExistenceCond;
import com.dubu.backend.todo.domain.dto.TodoSearchCond;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface TodoQueryRepository {
    List<Long> findTodoIds(TodoSearchCond cond);
    List<TodoInfo> findTodos(TodoSearchCond cond);
    Slice<TodoInfo> findTodosPaged(TodoCursor cursor, TodoSearchCond cond, Pageable pageable);
    List<TodoChildExistenceInfo> findTodoChildExistence(List<Long> todoIds, TodoChildExistenceCond cond);
}
