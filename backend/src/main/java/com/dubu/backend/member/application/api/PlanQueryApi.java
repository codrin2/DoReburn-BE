package com.dubu.backend.member.application.api;

import com.dubu.backend.member.domain.model.Category;
import com.dubu.backend.member.domain.model.TempMember;

import java.util.List;

public interface PlanQueryApi {
    List<Category> getRecentPlanTodoCategories(TempMember member);
}
