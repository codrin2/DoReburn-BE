package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Plan;

import java.util.List;

public interface CustomPathRepository {
    List<Path> findByPlanWithTodosOrderByPathOrder(Plan plan);
}