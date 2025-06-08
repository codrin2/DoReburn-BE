package com.dubu.backend.plan.infrastructure;

import com.dubu.backend.core.domain.event.FeedbackEndedEvent;
import com.dubu.backend.core.domain.event.PlanCreatedEvent;
import com.dubu.backend.core.domain.event.PlanEndedEvent;
import com.dubu.backend.core.domain.event.PlanRemovedEvent;
import com.dubu.backend.plan.application.event.PlanEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringPlanEventPublisher implements PlanEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishPlanCreatedEvent(PlanCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publishPlanEndedEvent(PlanEndedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publishPlanRemovedEvent(PlanRemovedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publishFeedbackEndedEvent(FeedbackEndedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
