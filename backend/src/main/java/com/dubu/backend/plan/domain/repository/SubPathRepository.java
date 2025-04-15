package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.todo.domain.enums.TodoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SubPathRepository extends JpaRepository<SubPath, Long>, CustomPathRepository {
    List<SubPath> findByPlanIdOrderByPathOrderAsc(Long id);

    @Query("SELECT p FROM SubPath p JOIN FETCH p.todos t WHERE p.plan in :plans AND t.type = :type AND t.isCompleted = :isCompleted")
    List<SubPath> findByPlansAndTypeAndIsCompleted(List<Plan> plans, TodoType type, boolean isCompleted);

    @Query("SELECT p FROM SubPath p JOIN FETCH p.todos t WHERE p.plan = :plan AND t.type = :type")
    List<SubPath> findByPlanAndType(Plan plan, TodoType type);

    @Query("SELECT p FROM SubPath p JOIN FETCH p.todos t WHERE p.plan = :plan AND t.isCompleted = :isCompleted")
    List<SubPath> findPathsByPlanAndIsCompleted(Plan plan, boolean isCompleted);

    @Query("SELECT pa.id FROM SubPath pa join pa.plan pl WHERE pl.member = :member AND pl.createdAt BETWEEN :startTime AND :endTime")
    List<Long> findPathIdsByMemberAndCreatedAtBetween(Member member, LocalDateTime startTime, LocalDateTime endTime);
}