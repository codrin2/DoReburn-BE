package com.dubu.backend.todo.application.impl.share;

import com.dubu.backend.member.domain.MemberLocation;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.infrastructure.RedisMemberLocationRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.todo.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.todo.application.ShareService;
import com.dubu.backend.todo.application.collection.share.MemberCategoryCollection;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.dto.request.CategoryRankRequest;
import com.dubu.backend.todo.dto.response.*;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.CellCategoryCountRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final RedisMemberLocationRepository redisMemberLocationRepository;
    private final CellCategoryCountRepository cellCategoryCountRepository;

    private final TempMemberRepository tempMemberRepository;
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    @Transactional
    public ShareInfo findSurroundingMembersInfo(Long memberId, SurroundingMemberQueryRequest request){
        memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        List<Category> categories = categoryRepository.findAll();

        List<MemberLocationInfo> memberLocationInfos = redisMemberLocationRepository.findMemberLocations(memberId, request);

        if(memberLocationInfos == null || memberLocationInfos.isEmpty()){
            return null;
        }

        List<MemberCategoryInfo> memberCategoryInfos = todoRepository.findTodoCountGroupByCategory(extractMemberIds(memberLocationInfos));

        MemberCategoryCollection memberCategoryCollection = new MemberCategoryCollection(memberCategoryInfos);

        redisMemberLocationRepository.saveMemberLocation(memberId, new MemberLocation(request.x_coordinate(), request.y_coordinate()));

        return ShareInfo.of(MemberInfo.from(memberLocationInfos, memberCategoryCollection.getMemberCategoryMap()), CategoryRankInfo.from(memberCategoryCollection.getCategoryMemberCountMap()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurroundingMemberLocationInfo> findSurroundingTempMembers(Long memberId, SurroundingMemberQueryRequest request) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        List<Category> categories = categoryRepository.findAll();

        List<TempMember> surroundingMemberIds = tempMemberRepository.findByLocation(geometryFactory.createPoint(new Coordinate(request.x_coordinate(), request.y_coordinate())), request.radius())
                .stream()
                .filter(m -> !m.getId().equals(memberId))
                .toList();

        List<MemberCategoryInfo> memberCategoryInfos = todoRepository.findTodoCountGroupByCategory(extractTempMemberIds(surroundingMemberIds));
        MemberCategoryCollection memberCategoryCollection = new MemberCategoryCollection(memberCategoryInfos);

        Map<Long, List<String>> memberCategoryMap = memberCategoryCollection.getMemberCategoryMap();

        return surroundingMemberIds.stream()
                .map(m -> {
                    Point location = m.getLocation();
                    return SurroundingMemberLocationInfo.of(m.getId(), location.getX(), location.getY(), memberCategoryMap.get(m.getId()));
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryRankInfo> findCategoryRank(Long memberId, CategoryRankRequest request) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        Map<String, GeoHash> bbCornergeoHashMap = GeoSpatialUtils.deriveBoundingBoxCornerGeoHash(request.y_coordinate(), request.x_coordinate(), request.radius());

        List<GeoHash> geoHashes = GeoSpatialUtils.deriveGeoHashInBoundingBox(bbCornergeoHashMap.get("NW"), bbCornergeoHashMap.get("NE"), bbCornergeoHashMap.get("SW"), request.x_coordinate(), request.y_coordinate(), request.radius());

        List<CategoryCountInfo> categoryCountInfos = null;
                // cellCategoryCountRepository.findByCellIds(geoHashes.stream().map(GeoHash::toBase32).toList());

        return CategoryRankInfo.from(categoryCountInfos);
        return ShareInfo.of(MemberInfo.from(memberLocationInfos, memberCategoryCollection.getMemberCategoryMap()), CategoryRankInfo.from(memberCategoryCollection.getCategoryMemberCountMap()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurroundingMemberLocationInfo> findSurroundingTempMembers(Long memberId, SurroundingMemberQueryRequest request) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        List<Category> categories = categoryRepository.findAll();

        List<TempMember> surroundingMemberIds = tempMemberRepository.findByLocation(geometryFactory.createPoint(new Coordinate(request.x_coordinate(), request.y_coordinate())), request.radius())
                .stream()
                .filter(m -> !m.getId().equals(memberId))
                .toList();

        List<MemberCategoryInfo> memberCategoryInfos = todoRepository.findTodoCountGroupByCategory(extractTempMemberIds(surroundingMemberIds));
        MemberCategoryCollection memberCategoryCollection = new MemberCategoryCollection(memberCategoryInfos);

        Map<Long, List<String>> memberCategoryMap = memberCategoryCollection.getMemberCategoryMap();

        return surroundingMemberIds.stream()
                .map(m -> {
                    Point location = m.getLocation();
                    return SurroundingMemberLocationInfo.of(m.getId(), location.getX(), location.getY(), memberCategoryMap.get(m.getId()));
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryRankInfo> findCategoryRank(Long memberId, CategoryRankRequest request) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        Map<String, GeoHash> bbCornergeoHashMap = GeoSpatialUtils.deriveBoundingBoxCornerGeoHash(request.y_coordinate(), request.x_coordinate(), request.radius());

        List<GeoHash> geoHashes = GeoSpatialUtils.deriveGeoHashInBoundingBox(bbCornergeoHashMap.get("NW"), bbCornergeoHashMap.get("NE"), bbCornergeoHashMap.get("SW"), request.x_coordinate(), request.y_coordinate(), request.radius());

        List<CategoryCountInfo> categoryCountInfos = null;
                // cellCategoryCountRepository.findByCellIds(geoHashes.stream().map(GeoHash::toBase32).toList());

        return CategoryRankInfo.from(categoryCountInfos);
    }

    private List<Long> extractMemberIds(List<MemberLocationInfo> memberLocationInfos) {
        return memberLocationInfos.stream()
                .map(MemberLocationInfo::memberId)
                .toList();
    }

    private List<Long> extractTempMemberIds(List<TempMember> members) {
        return members.stream()
                .map(TempMember::getId)
                .toList();
    }
}