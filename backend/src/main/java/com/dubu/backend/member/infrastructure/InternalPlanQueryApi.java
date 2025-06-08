package com.dubu.backend.member.infrastructure;

import com.dubu.backend.member.application.api.PlanQueryApi;
import com.dubu.backend.member.domain.model.Category;
import com.dubu.backend.member.domain.model.TempMember;
import com.dubu.backend.plan.api.response.RecentPlanTodosResponse;
import com.dubu.backend.plan.application.PlanFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InternalPlanQueryApi implements PlanQueryApi {
    private final PlanFacade planFacade;

    @Override
    public List<Category> getRecentPlanTodoCategories(TempMember member) {
        RecentPlanTodosResponse response = planFacade.findTodosOfRecentPlan(member.getId());

        return extractCategory(response);
    }

    private List<Category> extractCategory(RecentPlanTodosResponse response){
//        return response.todos()
//                .stream()
//                .map(t -> new Category(t.getCategory().id()))
//                .distinct()
//                .sorted(Comparator.comparing(Category::id))
//                .toList();
        return null;
    }
}
