package com.dubu.backend.member.application;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.member.exception.MemberNotFoundException;

public final class MemberServiceHelper {
    public static Member findExistingMember(MemberRepository memberRepository, Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }
}