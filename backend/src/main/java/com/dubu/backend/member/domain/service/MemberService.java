package com.dubu.backend.member.domain.service;

import com.dubu.backend.member.api.response.UserInfo;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.enums.OauthProvider;
import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member findExistingMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    public Member findOrCreateMember(UserInfo userInfo) {
        return memberRepository.findByEmail(userInfo.email())
                .orElseGet(() -> createMember(userInfo));
    }

    private Member createMember(UserInfo userInfo) {
        Member newMember = Member.of(
                userInfo.email(),
                OauthProvider.KAKAO,
                userInfo.oauthProviderId()
        );
        return memberRepository.save(newMember);
    }
}
