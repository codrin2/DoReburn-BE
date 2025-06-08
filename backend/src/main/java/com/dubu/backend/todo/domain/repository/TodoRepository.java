package com.dubu.backend.todo.domain.repository;

import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoQueryRepository {
    long countByScheduleId(Long scheduleId);
    long countBySubPathId(Long subPathId);

    boolean existsByScheduleIdAndParentInfoParentId(Long scheduleId, Long parentId);
    boolean existsByMemberIdAndParentInfoParentIdAndType(Long memberId, Long parentId, TodoType type);
    boolean existsBySubPathIdAndParentInfoParentId(Long subPathId, Long parentTodoId);

    Optional<Todo> findByMemberIdAndParentInfoParentId(Long memberId, Long parentId);

    List<Todo> findByIdIn(List<Long> ids);
    List<Todo> findByScheduleId(Long scheduleId);
    List<Todo> findBySubPathId(Long subPathId);

    @Query("SELECT t FROM Todo t JOIN FETCH t.category WHERE t.subPathId in :subPathIds")
    List<Todo> findBySubPathIdIn(List<Long> subPathIds);

    @Query("SELECT t FROM Todo t JOIN FETCH t.category WHERE t.subPathId in :subPathIds AND t.isCompleted = :isCompleted")
    List<Todo> findBySubPathIdInAndIsCompleted(List<Long> subPathIds, boolean isCompleted);

}
