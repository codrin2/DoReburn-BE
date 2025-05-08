package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.Member;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.repository.dto.PlanSearchCond;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomPlanRepository {
    List<Plan> findPlans(Member member, PlanSearchCond cond);
}
