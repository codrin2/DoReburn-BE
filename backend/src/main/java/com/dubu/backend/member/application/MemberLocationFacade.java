package com.dubu.backend.member.application;


import ch.hsr.geohash.GeoHash;
import com.dubu.backend.member.api.request.CellCategoryUpdateByLocationRequest;
import com.dubu.backend.member.api.request.CellCategoryUpdateByPlanRequest;
import com.dubu.backend.member.application.api.PlanQueryApi;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.model.Category;
import com.dubu.backend.member.domain.model.MemberLocation;
import com.dubu.backend.member.domain.model.TempMember;
import com.dubu.backend.member.application.event.MemberLocationUpdatedEvent;
import com.dubu.backend.member.domain.repository.MemberLocationRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.member.domain.repository.TempMemberRepository;

import com.dubu.backend.member.domain.service.CellCategoryManagementService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dubu.backend.member.application.MemberServiceHelper.findExistingMember;

@Service
@RequiredArgsConstructor
public class MemberLocationFacade {
    private final MemberRepository memberRepository;
    private final MemberLocationRepository memberLocationRepository;
    private final TempMemberRepository tempMemberRepository;
    private final PlanQueryApi planQueryApi;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final ApplicationEventPublisher eventPublisher;
    private final CellCategoryManagementService cellCategoryManagementService;

    public void updateMemberLocation(Long memberId, MemberLocation location) {
        findExistingMember(memberRepository, memberId);
        memberLocationRepository.saveMemberLocation(memberId, location);
    }

    @Transactional
    public void updateTempMemberLocation(Long memberId, MemberLocation location){
        TempMember member = tempMemberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        GeoHash oldGeoHash = null;
        if(member.hasLocation()){
            Point oldLocation = member.getLocation();
            oldGeoHash = GeoHash.withCharacterPrecision(oldLocation.getY(), oldLocation.getX(), 7);
        }

        Point newLocation = geometryFactory.createPoint(new Coordinate(location.x_coordinate(), location.y_coordinate()));
        GeoHash newGeoHash = GeoHash.withCharacterPrecision(newLocation.getY(), newLocation.getX(), 7);

        if(oldGeoHash == null || !oldGeoHash.equals(newGeoHash)){
            eventPublisher.publishEvent(new MemberLocationUpdatedEvent(memberId, oldGeoHash, newGeoHash));
        }

        member.updateLocation(newLocation);
    }

    @Transactional
    public void updateCellCategoryByLocationChange(Long memberId, CellCategoryUpdateByLocationRequest request){
        TempMember member = tempMemberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        List<Category> categories = planQueryApi.getRecentPlanTodoCategories(member);

        cellCategoryManagementService.updateCellCategoryByLocationChange(request.oldGeoHash(), request.newGeoHash(), categories);
    }

    @Transactional
    public void updateCellCategoryByPlanChange(Long memberId, CellCategoryUpdateByPlanRequest request){
        TempMember member = tempMemberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        cellCategoryManagementService.updateCellCategoryByPlanChange(member.getLocation(), request.beforeCategoryIds(), request.recentCategoryIds());
    }
}