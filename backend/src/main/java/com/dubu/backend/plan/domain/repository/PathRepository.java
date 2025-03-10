package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.todo.domain.enums.TodoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PathRepository extends JpaRepository<Path, Long>, CustomPathRepository {
    List<Path> findByPlanIdOrderByPathOrderAsc(Long id);

    @Query("SELECT p FROM Path p JOIN FETCH p.todos t WHERE p.plan in :plans AND t.type = :type AND t.isCompleted = :isCompleted")
    List<Path> findByPlansAndTypeAndIsCompleted(List<Plan> plans, TodoType type, boolean isCompleted);

    @Query("SELECT p FROM Path p JOIN FETCH p.todos t WHERE p.plan = :plan AND t.type = :type")
    List<Path> findByPlanAndType(Plan plan, TodoType type);

    @Query("SELECT p FROM Path p JOIN FETCH p.todos t WHERE p.plan = :plan AND t.isCompleted = :isCompleted")
    List<Path> findPathsByPlanAndIsCompleted(Plan plan, boolean isCompleted);

    @Query("SELECT pa.id FROM Path pa join pa.plan pl WHERE pl.member = :member AND pl.createdAt BETWEEN :startTime AND :endTime")
    List<Long> findPathIdsByMemberAndCreatedAtBetween(Member member, LocalDateTime startTime, LocalDateTime endTime);
}