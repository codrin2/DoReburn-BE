package com.dubu.backend.member.application;

import com.dubu.backend.member.application.event.MovementCompletedEvent;
import com.dubu.backend.member.domain.enums.MemberStatus;
import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.domain.service.MemberInfoService;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.member.api.request.MemberInfoUpdateRequest;
import com.dubu.backend.member.api.request.MemberOnboardingRequest;
import com.dubu.backend.member.api.response.MemberInfoResponse;
import com.dubu.backend.member.core.exception.InvalidMemberStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dubu.backend.member.application.MemberServiceHelper.findExistingMember;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandFacade {
    private final MemberRepository memberRepository;
    private final MemberInfoService memberInfoService;

    public void completeOnboarding(Long memberId, MemberOnboardingRequest request) {
        Member currentMember = findExistingMember(memberRepository, memberId);

        if (!currentMember.isOnboarding()) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        memberInfoService.completeOnboarding(currentMember, request);
    }

    public MemberInfoResponse updateMemberInfo(Long memberId, MemberInfoUpdateRequest request) {
        Member currentMember = findExistingMember(memberRepository, memberId);

        return memberInfoService.updateMemberInfo(currentMember, request);
    }

    public void updateMemberStatus(Long memberId, String status) {
        Member currentMember = findExistingMember(memberRepository, memberId);

        currentMember.updateStatus(MemberStatus.fromString(status));
    }

    public void updateMemberStatusByPlanChange(MovementCompletedEvent event) {
        Member currentMember = findExistingMember(memberRepository, event.memberId());

        memberInfoService.updateMemberStatusByPlanChange(currentMember, event.planId());
    }
}
