package com.dubu.backend.auth.application;

import com.dubu.backend.auth.domain.OauthProvider;
import com.dubu.backend.auth.dto.AccessTokenResponse;
import com.dubu.backend.auth.dto.KakaoUserInfo;
import com.dubu.backend.auth.dto.TokenResponse;
import com.dubu.backend.member.domain.Member;
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
    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenResponse kakaoLogin(String code) {
        String accessToken = oauthApi.getAccessToken(code);
        KakaoUserInfo userInfo = oauthApi.getOauthUser(accessToken);

        Member member = memberRepository.findByOauthProviderId(userInfo.oauthProviderId())
                .orElseGet(() -> creatMember(userInfo));

        return tokenService.issue(member.getId());
    }

    public TokenResponse reissueToken(String oldRefreshToken) {
        TokenResponse tokenResponse = tokenService.reissue(oldRefreshToken);

        return tokenResponse;
    }

    public AccessTokenResponse issueTokenForTest() {
        TokenResponse tokenResponse = tokenService.issue(1L);

        return new AccessTokenResponse(tokenResponse.accessToken());
    }

    private Member creatMember(KakaoUserInfo userInfo) {
        Member newMember = Member.of(
                userInfo.email(),
                OauthProvider.KAKAO,
                userInfo.oauthProviderId()
        );
        return memberRepository.save(newMember);
    }
}