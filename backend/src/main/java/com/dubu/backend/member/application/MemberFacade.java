package com.dubu.backend.member.application;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.MemberInfoService;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.member.presentation.request.MemberInfoUpdateRequest;
import com.dubu.backend.member.presentation.request.MemberOnboardingRequest;
import com.dubu.backend.member.presentation.response.MemberInfoResponse;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dubu.backend.member.application.MemberServiceHelper.findExistingMember;

@Service
@RequiredArgsConstructor
public class MemberFacade {
    private final MemberRepository memberRepository;
    private final MemberInfoService memberInfoService;

    @Transactional
    public void completeOnboarding(Long memberId, MemberOnboardingRequest request) {
        Member currentMember = findExistingMember(memberRepository, memberId);

        if (currentMember.isOnboarding()) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        memberInfoService.completeOnboarding(currentMember, request);
    }

    @Transactional
    public MemberInfoResponse updateMemberInfo(Long memberId, MemberInfoUpdateRequest request) {
        Member currentMember = findExistingMember(memberRepository, memberId);

        return memberInfoService.updateMemberInfo(currentMember, request);
    }

    @Transactional
    public void updateMemberStatus(Long memberId, String status) {
        Member currentMember = findExistingMember(memberRepository, memberId);

        currentMember.updateStatus(Status.fromString(status));
    }

    @Transactional
    public void updateMemberStatusByPlanChange(MovementCompletedEvent event) {
        Member currentMember = findExistingMember(memberRepository, event.memberId());

        memberInfoService.updateMemberStatusByPlanChange(currentMember, event.planId());
    }
}