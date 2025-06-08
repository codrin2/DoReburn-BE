package com.dubu.backend.member.infrastructure.event;

import com.dubu.backend.core.domain.event.FeedbackEndedEvent;
import com.dubu.backend.core.domain.event.PlanCreatedEvent;
import com.dubu.backend.core.domain.event.PlanEndedEvent;
import com.dubu.backend.member.application.MemberCommandFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanEventHandler {
    private final MemberCommandFacade memberCommandFacade;

    @EventListener
    public void handle(PlanCreatedEvent event){
        memberCommandFacade.updateMemberStatus(event.memberId(), "MOVE");
    }

    @EventListener
    public void handle(PlanEndedEvent event){
        memberCommandFacade.updateMemberStatus(event.memberId(), "FEEDBACK");
    }

    @EventListener
    public void handle(FeedbackEndedEvent event) {
        memberCommandFacade.updateMemberStatus(event.memberId(), "STOP");
    }
}
