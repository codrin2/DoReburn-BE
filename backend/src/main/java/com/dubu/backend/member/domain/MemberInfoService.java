package com.dubu.backend.member.domain;

import com.dubu.backend.member.domain.enums.AddressType;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.domain.repository.AddressRepository;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.member.api.request.MemberInfoUpdateRequest;
import com.dubu.backend.member.api.request.MemberOnboardingRequest;
import com.dubu.backend.member.api.response.MemberInfoResponse;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.exception.CategoryNotFoundException;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberInfoService {
    private final MemberCategoryRepository memberCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final AddressRepository addressRepository;
    private final PlanRepository planRepository;

    /**
     * 온보딩 완료(카테고리 저장, 주소 저장, 닉네임 & 상태 업데이트)
     */
    public void completeOnboarding(Member member, MemberOnboardingRequest request) {
        saveMemberCategories(member, request.categories());

        saveAddress(member, AddressType.HOME,
                request.homeTitle(), request.homeAddress(), request.homeAddressX(), request.homeAddressY());
        saveAddress(member, AddressType.SCHOOL,
                request.schoolTitle(), request.schoolAddress(), request.schoolAddressX(), request.schoolAddressY());

        member.updateNickname(request.nickname());
        member.updateStatus(Status.STOP);
    }

    /**
     * 회원 정보 업데이트
     */
    public MemberInfoResponse updateMemberInfo(Member member, MemberInfoUpdateRequest request) {
        updateMemberCategories(member, request.categories());

        updateAddressInfo(member, AddressType.HOME,
                request.homeTitle(), request.homeAddress(), request.homeAddressX(), request.homeAddressY());
        updateAddressInfo(member, AddressType.SCHOOL,
                request.schoolTitle(), request.schoolAddress(), request.schoolAddressX(), request.schoolAddressY());

        List<Address> updatedAddresses = addressRepository.findByMemberId(member.getId());
        List<Category> updatedCategories = memberCategoryRepository.findByMemberId(member.getId())
                .stream()
                .map(MemberCategory::getCategory)
                .toList();

        return MemberInfoResponse.of(member, updatedCategories, updatedAddresses);
    }

    /**
     * Plan이 완료되지 않았다면 FEEDBACK 상태로 변경
     */
    public void updateMemberStatusByPlanChange(Member member, Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException(planId));

        if (!plan.isCompleted()) {
            member.updateStatus(Status.FEEDBACK);
        }
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

    private void saveAddress(Member member, AddressType type, String title,
                             String roadAddress, Double x, Double y) {
        Address address = Address.createAddress(member, type, title, roadAddress, x, y);
        addressRepository.save(address);
    }

    private void updateMemberCategories(Member member, List<String> requestedCats) {
        List<MemberCategory> existingCats = memberCategoryRepository.findByMemberId(member.getId());

        Set<String> existingNames = existingCats.stream()
                .map(mc -> mc.getCategory().getName())
                .collect(Collectors.toSet());
        Set<String> requestedNames = new HashSet<>(requestedCats);

        // 기존 존재하지만 요청에 없는 카테고리 삭제
        existingCats.stream()
                .filter(mc -> !requestedNames.contains(mc.getCategory().getName()))
                .forEach(memberCategoryRepository::delete);

        // 새로 요청된(기존에 없던) 카테고리 추가
        requestedNames.stream()
                .filter(catName -> !existingNames.contains(catName))
                .forEach(catName -> {
                    Category category = categoryRepository.findByName(catName)
                            .orElseThrow(() -> new CategoryNotFoundException(catName));
                    MemberCategory newMC = MemberCategory.builder()
                            .member(member)
                            .category(category)
                            .build();
                    memberCategoryRepository.save(newMC);
                });
    }

    private void updateAddressInfo(Member member, AddressType type, String title,
                                   String roadAddress, Double x, Double y) {
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
}