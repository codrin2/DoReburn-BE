package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long>, CustomPlanRepository {
    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.feedback WHERE p.memberId = :memberId ORDER BY p.createdAt DESC LIMIT 1")
    Optional<Plan> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("SELECT p FROM Plan p WHERE p.memberId = :memberId AND p.isCompleted = :isCompleted ORDER BY p.createdAt DESC LIMIT 1")
    Optional<Plan> findTopByMemberIdAndIsCompletedOrderByCreatedAtDesc(Long memberId, boolean isCompleted);
}