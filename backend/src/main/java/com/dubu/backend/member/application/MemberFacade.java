package com.dubu.backend.member.application;

import com.dubu.backend.member.domain.Address;
import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.MemberCategory;
import com.dubu.backend.member.domain.enums.AddressType;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.presentation.MemberLocationDto;
import com.dubu.backend.member.presentation.MemberStatusChangeDto;
import com.dubu.backend.member.presentation.request.MemberInfoUpdateRequest;
import com.dubu.backend.member.presentation.request.MemberOnboardingRequest;
import com.dubu.backend.member.presentation.response.MemberInfoResponse;
import com.dubu.backend.member.presentation.response.MemberSavedAddressResponse;
import com.dubu.backend.member.presentation.response.MemberStatusResponse;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.exception.MemberSavedAddressNotFoundException;
import com.dubu.backend.member.exception.RedisUnavailableException;
import com.dubu.backend.member.domain.repository.AddressRepository;
import com.dubu.backend.member.infrastructure.redis.LocationRedisRepository;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.exception.CategoryNotFoundException;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberFacade {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final MemberCategoryRepository memberCategoryRepository;
    private final AddressRepository addressRepository;
    private final PlanRepository planRepository;
    private final LocationRedisRepository locationRedisRepository;

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
}