package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.repository.dto.PlanSearchCond;

import java.util.List;

public interface CustomPlanRepository {
    List<Plan> findPlans(PlanSearchCond cond);
    List<Long> findCompletedRecentPlansId(List<Long> memberIds);
}
