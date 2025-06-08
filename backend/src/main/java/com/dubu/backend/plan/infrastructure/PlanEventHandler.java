package com.dubu.backend.plan.infrastructure;

import com.dubu.backend.plan.core.exception.PlanNotFoundException;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.repository.PlanRepository;
import com.dubu.backend.plan.infrastructure.api.CellCategoryCommandApi;
import com.dubu.backend.core.domain.event.PlanEndedEvent;
import com.dubu.backend.todo.domain.past.Todo;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
@RequiredArgsConstructor
public class PlanEventHandler {
    private final TodoRepository todoRepository;
    private final PlanRepository planRepository;
    private final CellCategoryCommandApi cellCategoryCommandApi;

//    @Async
//    @EventListener(PlanEndedEvent.class)
    public void handlePlanEndedEvent(PlanEndedEvent event){
//        Plan beforePlan = planRepository.findTopByMemberIdAndIsCompletedAndIdNotOrderByCreatedAtDesc(event.memberId(), true, event.plan().getId())
//                .orElseThrow(PlanNotFoundException::new);
//
//        List<Todo> beforeTodos = todoRepository.findByPlanAndIsCompleted(beforePlan, true);
//        List<Todo> recentTodos = todoRepository.findByPlanAndIsCompleted(event.plan(), true);
//
//        cellCategoryCommandApi.updateCellCategory(event.memberId(), beforeTodos, recentTodos);
    }
}
