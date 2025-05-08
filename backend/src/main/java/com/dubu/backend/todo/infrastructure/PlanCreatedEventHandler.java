package com.dubu.backend.todo.infrastructure;

import com.dubu.backend.plan.application.event.PlanCreatedEvent;
import com.dubu.backend.todo.application.TodoInitFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanCreatedEventHandler {
    private final TodoInitFacade todoInitFacade;

    @EventListener
    public void handler(PlanCreatedEvent event) {
        todoInitFacade.initPathTodos(event.memberId(), event.subPathId());
    }
}
