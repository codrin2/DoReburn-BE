package com.dubu.backend.plan.infrastructure;

import com.dubu.backend.member.api.request.CellCategoryUpdateByPlanRequest;
import com.dubu.backend.member.application.MemberLocationFacade;
import com.dubu.backend.plan.infrastructure.api.CellCategoryCommandApi;
import com.dubu.backend.todo.domain.past.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InternalCellCategoryCommandApi implements CellCategoryCommandApi {
    private final MemberLocationFacade memberLocationFacade;

    @Override
    public void updateCellCategory(Long memberId, List<Todo> beforeTodos, List<Todo> recentTodos) {
        memberLocationFacade.updateCellCategoryByPlanChange(memberId,
                new CellCategoryUpdateByPlanRequest(
                        extractCategoryIds(beforeTodos),
                        extractCategoryIds(recentTodos)
                ));
    }

    private List<Long> extractCategoryIds(List<Todo> todos){
        return todos.stream()
                .map(t -> t.getCategory().getId())
                .distinct()
                .toList();
    }
}
