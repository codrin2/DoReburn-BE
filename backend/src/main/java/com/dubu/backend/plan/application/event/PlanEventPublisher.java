package com.dubu.backend.plan.application.event;

import com.dubu.backend.core.domain.event.FeedbackEndedEvent;
import com.dubu.backend.core.domain.event.PlanCreatedEvent;
import com.dubu.backend.core.domain.event.PlanEndedEvent;
import com.dubu.backend.core.domain.event.PlanRemovedEvent;

public interface PlanEventPublisher {
    void publishPlanCreatedEvent(PlanCreatedEvent event);
    void publishPlanEndedEvent(PlanEndedEvent event);
    void publishPlanRemovedEvent(PlanRemovedEvent event);
    void publishFeedbackEndedEvent(FeedbackEndedEvent event);
}
