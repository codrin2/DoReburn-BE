package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.plan.domain.Plan;

import java.util.List;

public interface CustomPathRepository {
    List<SubPath> findByPlanWithTodosOrderByPathOrder(Plan plan);
}