package com.dubu.backend.todo.infra.repository;

import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.todo.dto.response.MemberCategoryInfo;
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
    @Query("SELECT t FROM Todo t WHERE t.subPath = :subPath")
    List<Todo> findTodosByPath(SubPath subPath);

    @Query("SELECT t FROM Todo t JOIN FETCH t.category WHERE t.subPath = :subPath")
    List<Todo> findTodosWithCategoryByPath(@Param("subPath") SubPath subPath);

    @Query("SELECT t.parentTodo.id FROM Todo t where t.subPath = :subPath AND t.parentTodo IS NOT NULL")
    List<Long> findParentTodoIdsByPathAndParentTodoNotNull(SubPath subPath);

    @Query("SELECT t FROM Todo t WHERE t.parentTodo = :parentTodo AND t.subPath = :subPath")
    Optional<Todo> findByParentTodoAndPath(@Param("parentTodo") Todo parentTodo, @Param("subPath") SubPath subPath);

    @Query("SELECT t FROM Todo t WHERE t.subPath.id IN :pathIds AND t.type = :type AND t.isCompleted = true")
    List<Todo> findByPathIdsAndTypeAndIsCompleted(List<Long> pathIds, TodoType type);

    // 계획으로 조회
    @Query("SELECT t FROM Todo t " +
            "join fetch t.category " +
            "join fetch t.subPath p " +
            "WHERE p.plan = :plan AND t.isCompleted = :isCompleted")
    List<Todo> findByPlanAndIsCompleted(Plan plan, Boolean isCompleted);

    @Query(value = """
        WITH ranked_plan AS (
            SELECT plan_id, row_number() over (partition by member_id order by created_at desc) as rn
            FROM plan
            WHERE member_id IN :memberIds AND is_completed <> 0
        )
            SELECT DISTINCT t.member_id, c.name
            FROM todo t
                     JOIN category c ON c.category_id = t.category_id
                     JOIN subPath p ON p.sub_path_id = t.sub_path_id
                     JOIN ranked_plan rp ON rp.plan_id = p.plan_id AND rp.rn = 1
            WHERE t.is_completed = true;
    """, nativeQuery = true)
    List<MemberCategoryInfo> findTodoCountGroupByCategory(List<Long> memberIds);
}

