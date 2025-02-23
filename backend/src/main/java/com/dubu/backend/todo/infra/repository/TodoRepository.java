package com.dubu.backend.todo.infra.repository;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.infra.repository.querydsl.CustomTodoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, CustomTodoRepository {
    // todoId로 조회
    @Query("SELECT t FROM Todo t JOIN FETCH t.category c WHERE t.id = :todoId")
    Optional<Todo> findWithCategoryById(@Param("todoId") Long todoId);

    @Query("SELECT t FROM Todo t JOIN FETCH t.category WHERE t.id IN :todoIds")
    List<Todo> findTodosWithCategoryByIds(List<Long> todoIds);

    // 스케줄, 날짜로 조회
    @Query("SELECT t FROM Todo t JOIN FETCH t.category WHERE t.schedule = :schedule")
    List<Todo> findTodosWithCategoryBySchedule(@Param("schedule")Schedule schedule);

    @Query("SELECT t FROM Todo t JOIN t.schedule s WHERE t.parentTodo = :todo AND s.date = :date")
    Optional<Todo> findWithScheduleByParentTodoAndScheduleDate(@Param("todo") Todo parentTodo, @Param("date")LocalDate date);

    @Query("SELECT t FROM Todo t WHERE t.parentTodo = :parentTodo AND t.schedule = :schedule")
    Optional<Todo> findByParentTodoAndSchedule(@Param("parentTodo") Todo parentTodo, @Param("schedule") Schedule schedule);

    @Query("SELECT t.parentTodo.id FROM Todo t WHERE t.schedule = :schedule AND t.parentTodo IS NOT NULL")
    List<Long> findParentTodoIdsByScheduleAndParentTodoNotNull(Schedule schedule);

    // type 으로 조회
    @Query("SELECT t.parentTodo.id FROM Todo t WHERE t.parentTodo IN :parentTodos AND t.member = :member AND t.type = :type")
    List<Long> findParentTodoIdsByParentTodoAndMemberAndType(@Param("parentTodos") List<Todo> parentTodos, @Param("member") Member member, @Param("type") TodoType type);

    @Query("SELECT t.parentTodo.id FROM Todo t WHERE t.id IN :ids AND t.type = :type AND t.parentTodo IS NOT NULL")
    List<Long> findParentTodoIdsByIdsAndTypeAndParentTodoNotNull(List<Long> ids, TodoType type);

    @Query("SELECT t From Todo t WHERE t.member = :member AND t.parentTodo = :parentTodo AND t.type = :type")
    Optional<Todo> findByMemberAndParentTodoAndType(@Param("member") Member member, @Param("parentTodo") Todo parentTod, @Param("type") TodoType type);

    // 경로로 조회
    @Query("SELECT t FROM Todo t WHERE t.path = :path")
    List<Todo> findTodosByPath(Path path);

    @Query("SELECT t FROM Todo t JOIN FETCH t.category WHERE t.path = :path")
    List<Todo> findTodosWithCategoryByPath(@Param("path") Path path);

    @Query("SELECT t.parentTodo.id FROM Todo t where t.path = :path AND t.parentTodo IS NOT NULL")
    List<Long> findParentTodoIdsByPathAndParentTodoNotNull(Path path);

    @Query("SELECT t FROM Todo t WHERE t.parentTodo = :parentTodo AND t.path = :path")
    Optional<Todo> findByParentTodoAndPath(@Param("parentTodo") Todo parentTodo, @Param("path") Path path);

    @Query("SELECT t FROM Todo t WHERE t.path.id IN :pathIds AND t.type = :type AND t.isCompleted = true")
    List<Todo> findByPathIdsAndTypeAndIsCompleted(List<Long> pathIds, TodoType type);
}

