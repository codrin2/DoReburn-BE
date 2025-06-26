package com.dubu.backend.member.application;

import com.dubu.backend.member.api.response.AccessToken;
import com.dubu.backend.member.api.response.Token;
import com.dubu.backend.member.api.response.UserInfo;
import com.dubu.backend.member.application.api.OauthApi;
import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.domain.service.MemberService;
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
    private final MemberService memberService;

    @Transactional
    public Token kakaoLogin(String code) {
        String accessToken = oauthApi.getAccessToken(code);
        UserInfo userInfo = oauthApi.getOauthUser(accessToken);

        Member member = memberService.findOrCreateMember(userInfo);

        return tokenFacade.issue(member.getId());
    }

    public Token reissueToken(String oldRefreshToken) {
        return tokenFacade.reissue(oldRefreshToken);
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
}
