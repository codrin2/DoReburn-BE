package com.dubu.backend.member.application;

import ch.hsr.geohash.GeoHash;
import com.dubu.backend.member.domain.Address;
import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.MemberCategory;
import com.dubu.backend.member.domain.TempMember;
import com.dubu.backend.member.domain.enums.AddressType;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.dto.MemberLocationDto;
import com.dubu.backend.member.dto.MemberStatusChangeDto;
import com.dubu.backend.member.dto.request.MemberInfoUpdateRequest;
import com.dubu.backend.member.dto.request.MemberOnboardingRequest;
import com.dubu.backend.member.dto.response.MemberInfoResponse;
import com.dubu.backend.member.dto.response.MemberSavedAddressResponse;
import com.dubu.backend.member.dto.response.MemberStatusResponse;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.exception.MemberSavedAddressNotFoundException;
import com.dubu.backend.member.exception.RedisUnavailableException;
import com.dubu.backend.member.infra.repository.*;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.CellCategoryCount;
import com.dubu.backend.todo.domain.CellCategoryCountId;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.exception.CategoryNotFoundException;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.CellCategoryCountRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final MemberCategoryRepository memberCategoryRepository;
    private final AddressRepository addressRepository;
    private final PlanRepository planRepository;
    private final LocationRedisRepository locationRedisRepository;

    private final TempMemberRepository tempMemberRepository;
    private final CellCategoryCountRepository cellCategoryCountRepository;
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public MemberInfoResponse findMemberInfo(Long memberId) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        List<Category> categories = memberCategoryRepository.findCategoriesByMemberId(memberId);

        List<Address> addresses = addressRepository.findByMemberId(memberId);

        return MemberInfoResponse.of(currentMember, categories, addresses);
    }

    @Transactional(readOnly = true)
    public MemberStatusResponse findMemberStatus(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        return new MemberStatusResponse(member.getStatus().name());
    }

    @Transactional(readOnly = true)
    public MemberSavedAddressResponse findMemberSavedAddress(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        List<Address> addresses = addressRepository.findByMemberId(memberId);
        if (addresses.isEmpty()) {
            throw new MemberSavedAddressNotFoundException(memberId);
        }

        return MemberSavedAddressResponse.from(addresses);
    }

    @Transactional(readOnly = true)
    public List<String> findMemberCategory(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        return memberCategoryRepository.findMemberCategoriesWithCategoryByMember(member)
                .stream().map(memberCategory -> memberCategory.getCategory().getName())
                .toList();
    }

    @Transactional
    public void completeOnboarding(Long memberId, MemberOnboardingRequest request) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (currentMember.getStatus() != Status.ONBOARDING) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        saveMemberCategories(currentMember, request.categories());

        saveAddress(currentMember, AddressType.HOME, request.homeTitle(),
                request.homeAddress(), request.homeAddressX(), request.homeAddressY());
        saveAddress(currentMember, AddressType.SCHOOL, request.schoolTitle(),
                request.schoolAddress(), request.schoolAddressX(), request.schoolAddressY());

        currentMember.updateNickname(request.nickname());
        currentMember.updateStatus(Status.STOP);
    }

    @Transactional
    public MemberInfoResponse updateMemberInfo(Long memberId, MemberInfoUpdateRequest request) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        updateMemberCategories(currentMember, request.categories());

        updateAddressInfo(currentMember, AddressType.HOME, request.homeTitle(),
                request.homeAddress(), request.homeAddressX(), request.homeAddressY());
        updateAddressInfo(currentMember, AddressType.SCHOOL, request.schoolTitle(),
                request.schoolAddress(), request.schoolAddressX(), request.schoolAddressY());

        List<Address> updatedAddresses = addressRepository.findByMemberId(memberId);
        List<Category> updatedCategories = memberCategoryRepository.findByMemberId(memberId)
                .stream()
                .map(MemberCategory::getCategory)
                .toList();

        return MemberInfoResponse.of(currentMember, updatedCategories, updatedAddresses);
    }

    @Transactional
    public void updateMemberStatus(Long memberId, String status) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        currentMember.updateStatus(Status.fromString(status));
    }

    @Transactional
    public void updateMemberStatusByPlanChange(MemberStatusChangeDto memberStatusChangeDto) {
        Member currentMember = memberRepository.findById(memberStatusChangeDto.memberId())
                .orElseThrow(() -> new MemberNotFoundException(memberStatusChangeDto.memberId()));

        Plan currentPlan = planRepository.findById(memberStatusChangeDto.planId())
                .orElseThrow(() -> new PlanNotFoundException(memberStatusChangeDto.planId()));

        if (currentPlan.isCompleted()) {
            return;
        }

        currentMember.updateStatus(Status.FEEDBACK);
    }

    private void saveMemberCategories(Member member, List<String> categoryNames) {
        List<MemberCategory> memberCategories = categoryNames.stream()
                .map(catName -> {
                    Category category = categoryRepository.findByName(catName)
                            .orElseThrow(() -> new CategoryNotFoundException(catName));
                    return MemberCategory.builder()
                            .member(member)
                            .category(category)
                            .build();
                })
                .toList();
        memberCategoryRepository.saveAll(memberCategories);
    }

    private void saveAddress(
            Member member,
            AddressType type,
            String title,
            String roadAddress,
            Double x,
            Double y
    ) {
        Address address = Address.createAddress(member, type, title, roadAddress, x, y);
        addressRepository.save(address);
    }

    private void updateMemberCategories(Member member, List<String> requestedCats) {
        List<MemberCategory> existingMemberCategories = memberCategoryRepository.findByMemberId(member.getId());

        Set<String> existingCategoryNames = existingMemberCategories.stream()
                .map(mc -> mc.getCategory().getName())
                .collect(Collectors.toSet());

        Set<String> requestedCategoryNames = new HashSet<>(requestedCats);

        existingMemberCategories.stream()
                .filter(mc -> !requestedCategoryNames.contains(mc.getCategory().getName()))
                .forEach(memberCategoryRepository::delete);

        // 새로 요청된 카테고리(기존에 없던 것) 추가
        requestedCategoryNames.stream()
                .filter(catName -> !existingCategoryNames.contains(catName))
                .forEach(catName -> {
                    Category category = categoryRepository.findByName(catName)
                            .orElseThrow(() -> new CategoryNotFoundException(catName));
                    MemberCategory newMemberCategory = MemberCategory.builder()
                            .member(member)
                            .category(category)
                            .build();
                    memberCategoryRepository.save(newMemberCategory);
                });
    }

    private void updateAddressInfo(
            Member member,
            AddressType type,
            String title,
            String roadAddress,
            Double x,
            Double y
    ) {
        List<Address> addresses = addressRepository.findByMemberId(member.getId());
        Address target = addresses.stream()
                .filter(addr -> addr.getAddressType() == type)
                .findFirst()
                .orElse(null);

        if (target != null) {
            target.updateAddress(title, roadAddress, x, y);
        } else {
            Address newAddress = Address.createAddress(member, type, title, roadAddress, x, y);
            addressRepository.save(newAddress);
        }
    }

    public void updateMemberLocation(Long memberId, MemberLocationDto location) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        try {
            locationRedisRepository.saveMemberLocation(memberId, location);
        }
        catch (RedisConnectionFailureException e) {
            throw new RedisUnavailableException();
        }
    }

    @Transactional
    public void updateTempMemberLocation(Long memberId, MemberLocationDto location){
        TempMember member = tempMemberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        Point prevLocation = member.getLocation();
        Point newLocation = geometryFactory.createPoint(new Coordinate(location.y_coordinate(), location.x_coordinate()));

        // 위치에 대한 GeoHash
        GeoHash prevGeoHash = null;
        GeoHash curGeoHash = GeoHash.withCharacterPrecision(location.y_coordinate(), location.x_coordinate(), 7);

        // 이전 위치와 현재 위치가 같은 geo hash 를 갖는 경우
        if (prevLocation != null) {
            prevGeoHash = GeoHash.withCharacterPrecision(prevLocation.getY(), prevLocation.getX(), 7);

            if (curGeoHash.equals(prevGeoHash)) {
                return;
            }
        }
        member.updateLocation(newLocation);

        updateCellCategoryCount(prevGeoHash, curGeoHash, memberId);
    }

    private List<Long> extractCategoryIds(List<Todo> todos){
        return todos.stream()
                .map(Todo::getCategory)
                .map(Category::getId)
                .distinct()
                .sorted()
                .toList();
    }

    private void updateCellCategoryCount(GeoHash prevGeoHash, GeoHash curGeoHash, Long memberId){
        // 최근 완료한 계획 조회
        Plan plan = planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(PlanNotFoundException::new);
        List<Todo> recentCompletedTodos = todoRepository.findByPlanAndIsCompleted(plan, true);
        List<Long> categoryIds = extractCategoryIds(recentCompletedTodos);

        // GeoHash 와 CategoryId 매칭되는 CellCategoryCount 찾기
        int diff = prevGeoHash.compareTo(curGeoHash);

        executeCellCategoryCountUpsertByFlag(diff, categoryIds, prevGeoHash, curGeoHash);
    }

    private List<GeoHash> compareGeoHash(GeoHash prevGeoHash, GeoHash curGeoHash){
        if(prevGeoHash != null){
            if (prevGeoHash.toBase32().compareTo(curGeoHash.toBase32()) < 0){
                return List.of(prevGeoHash, curGeoHash);
            }
        }
        return List.of(curGeoHash);
    }

    private void executeCellCategoryCountUpsertByFlag(int flag, List<Long> categoryIds, GeoHash prevGeoHash, GeoHash curGeoHash){
        if(flag > 0){
            categoryIds.forEach(categoryId -> {
               cellCategoryCountRepository.upsertCellCategoryCount(curGeoHash.toBase32(), categoryId, 1);
               cellCategoryCountRepository.upsertCellCategoryCount(prevGeoHash.toBase32(), categoryId, -1);
            });
            return;
        }

        categoryIds.forEach(categoryId -> {
            cellCategoryCountRepository.upsertCellCategoryCount(prevGeoHash.toBase32(), categoryId, -1);
            cellCategoryCountRepository.upsertCellCategoryCount(curGeoHash.toBase32(), categoryId, 1);
        });
    }
}