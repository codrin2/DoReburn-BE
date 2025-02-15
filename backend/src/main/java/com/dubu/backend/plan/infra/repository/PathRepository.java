package com.dubu.backend.plan.infra.repository;

import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.todo.entity.TodoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PathRepository extends JpaRepository<Path, Long>, CustomPathRepository {
    List<Path> findByPlanIdOrderByPathOrderAsc(Long id);

    @Query("SELECT p FROM Path p JOIN FETCH p.todos t WHERE p.plan in :plans AND t.type = :type AND t.isCompleted = :isCompleted")
    List<Path> findByPlanAndTypeAndIsCompleted(List<Plan> plans, TodoType type, boolean isCompleted);
}