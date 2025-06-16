package com.dubu.backend.todo.infrastructure.event;

import com.dubu.backend.member.application.event.MemberOnboardingEndedEvent;
import com.dubu.backend.todo.application.TodoInitFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberEventHandler {
    private final TodoInitFacade todoInitFacade;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberOnboardingEndedEvent event){
        todoInitFacade.initTodayTodos(event.memberId());
    }
}
