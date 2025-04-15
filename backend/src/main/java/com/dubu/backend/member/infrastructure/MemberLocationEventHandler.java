package com.dubu.backend.member.infrastructure;

import com.dubu.backend.member.api.request.CellCategoryUpdateByLocationRequest;
import com.dubu.backend.member.application.MemberLocationFacade;
import com.dubu.backend.member.application.event.MemberLocationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberLocationEventHandler {
    private final MemberLocationFacade memberLocationFacade;

    @EventListener(MemberLocationUpdatedEvent.class)
    public void handle(MemberLocationUpdatedEvent event){
        memberLocationFacade.updateCellCategoryByLocationChange(event.memberId(), new CellCategoryUpdateByLocationRequest(event.oldGeoHash(), event.newGeoHash()));
    }
}
