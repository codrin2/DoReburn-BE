package com.dubu.backend.member.application;

import com.dubu.backend.member.domain.MemberLocation;
import com.dubu.backend.member.domain.repository.MemberLocationRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dubu.backend.member.application.MemberServiceHelper.findExistingMember;

@Service
@RequiredArgsConstructor
public class MemberLocationFacade {
    private final MemberRepository memberRepository;
    private final MemberLocationRepository memberLocationRepository;

    public void updateMemberLocation(Long memberId, MemberLocation location) {
        findExistingMember(memberRepository, memberId);
        memberLocationRepository.saveMemberLocation(memberId, location);
    }
}