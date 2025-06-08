package com.dubu.backend.todo.infrastructure.event;

import com.dubu.backend.core.domain.event.PlanCreatedEvent;
import com.dubu.backend.core.domain.event.PlanEndedEvent;
import com.dubu.backend.todo.application.TodoCommandFacade;
import com.dubu.backend.todo.application.TodoInitFacade;
import com.dubu.backend.todo.application.dto.request.TodoCompleteCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component("todo.planEventHandler")
@RequiredArgsConstructor
public class PlanEventHandler {
    private final TodoInitFacade todoInitFacade;
    private final TodoCommandFacade todoCommandFacade;

    @EventListener
    public void handle(PlanCreatedEvent event) {
        todoInitFacade.initPathTodos(event.memberId(), event.subPathId());
    }

    @EventListener
    public void handle(PlanEndedEvent event){
        Map<Long, Integer> todoSpentTimeMap = event.doneTodos().stream().collect(Collectors.toMap(PlanEndedEvent.DoneTodo::todoId, PlanEndedEvent.DoneTodo::spentTime));
        todoCommandFacade.completeTodoOnPlanEnd(TodoCompleteCommand.of(event.todos(), todoSpentTimeMap));
    }
}
