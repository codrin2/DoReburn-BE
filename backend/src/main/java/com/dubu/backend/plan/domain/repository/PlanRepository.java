package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.feedback WHERE p.member.id = :memberId ORDER BY p.createdAt DESC LIMIT 1")
    Optional<Plan> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.feedback WHERE p.member= :member AND p.isCompleted = :isCompleted ORDER BY p.createdAt DESC LIMIT 1")
    Optional<Plan> findTopByMemberAndIsCompletedOrderByCreatedAtDesc(Member member, boolean isCompleted);

    @Query("SELECT p FROM Plan p JOIN FETCH p.feedback WHERE p.member = :member AND p.createdAt between :startTime AND :endTime")
    List<Plan> findByMemberAndCreatedAtBetween(Member member, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT p FROM Plan p JOIN FETCH p.feedback JOIN FETCH p.paths WHERE p.member = :member AND p.isCompleted = true AND p.createdAt between :startTime AND :endTime")
    List<Plan> findWithPathsByMemberAndCreatedAtBetween(Member member, LocalDateTime startTime, LocalDateTime endTime);

    // 통계 기능 최적화 작업을 위해 남겨둠
    @Query("SELECT pa.id FROM Plan p JOIN p.paths pa WHERE p.member = :member AND p.createdAt between :startTime AND :endTime")
    List<Long> findPathIdsWithPathsByMemberAndCreatedAtBetween(Member member, LocalDateTime startTime, LocalDateTime endTime);
}