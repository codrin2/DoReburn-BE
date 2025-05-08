package com.dubu.backend.todo.domain.repository;

import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoQueryRepository {
    long countByScheduleId(Long scheduleId);
    long countBySubPathId(Long subPathId);

    boolean existsByScheduleIdAndParentInfoParentId(Long scheduleId, Long parentId);
    boolean existsByMemberIdAndParentInfoParentIdAndType(Long memberId, Long parentId, TodoType type);
    boolean existsBySubPathIdAndParentInfoParentId(Long subPathId, Long parentTodoId);

    List<Todo> findByIdIn(List<Long> ids);
    List<Todo> findByScheduleId(Long scheduleId);
    List<Todo> findBySubPathId(Long subPathId);
    List<Todo> findByCategoryInAndType(List<Category> categories, TodoType type);


}
