package com.dubu.backend.member.application;

import com.dubu.backend.member.application.api.OauthApi;
import com.dubu.backend.member.domain.enums.OauthProvider;
import com.dubu.backend.member.api.response.AccessToken;
import com.dubu.backend.member.api.response.UserInfo;
import com.dubu.backend.member.api.response.Token;
import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade {
    private final OauthApi oauthApi;
    private final TokenFacade tokenFacade;
    private final MemberRepository memberRepository;

    @Transactional
    public Token kakaoLogin(String code) {
        String accessToken = oauthApi.getAccessToken(code);
        UserInfo userInfo = oauthApi.getOauthUser(accessToken);

        Member member = memberRepository.findByOauthProviderId(userInfo.oauthProviderId())
                .orElseGet(() -> creatMember(userInfo));

        return tokenFacade.issue(member.getId());
    }

    public Token reissueToken(String oldRefreshToken) {
        Token token = tokenFacade.reissue(oldRefreshToken);

        return token;
    }

    @Transactional
    public void deleteAccount(Long memberId) {
        Member member = memberService.findExistingMember(memberId);
        member.deactivate();
    }

    public AccessToken issueTokenForTest() {
        Token token = tokenFacade.issue(1L);

        return new AccessToken(token.accessToken());
    }

    private Member creatMember(UserInfo userInfo) {
        Member newMember = Member.of(
                userInfo.email(),
                OauthProvider.KAKAO,
                userInfo.oauthProviderId()
        );
        return memberRepository.save(newMember);
    }
}