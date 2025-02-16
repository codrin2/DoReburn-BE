package com.dubu.backend.plan.infra.repository;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.todo.entity.TodoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("SELECT p FROM Plan p JOIN FETCH p.feedback WHERE p.member = :member AND p.createdAt between :startTime AND :endTime")
    List<Plan> findByMemberAndCreatedAtBetween(Member member, LocalDateTime startTime, LocalDateTime endTime);
}