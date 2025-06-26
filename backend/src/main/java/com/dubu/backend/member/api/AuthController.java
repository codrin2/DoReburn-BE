package com.dubu.backend.member.api;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.member.api.response.AccessToken;
import com.dubu.backend.member.api.response.Token;
import com.dubu.backend.member.api.swagger.AuthSwagger;
import com.dubu.backend.member.application.AuthFacade;
import com.dubu.backend.member.application.TokenFacade;
import com.dubu.backend.member.core.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthSwagger {
    public static final long HOURS_IN_MINIUTES = 60 * 60L;

    private final JwtProperties jwtProperties;
    private final AuthFacade authFacade;
    private final TokenFacade tokenFacade;

    @PostMapping("/kakao-login")
    public SuccessResponse<AccessToken> kakaoLogin(
            @RequestBody Map<String, String> request,
            HttpServletResponse response
    ) {
        String code = request.get("code");
        Token token = authFacade.kakaoLogin(code);
        sendCookie(response, token.refreshToken());

        return new SuccessResponse<>(new AccessToken(token.accessToken()));
    }

    @PostMapping("/reissue")
    public SuccessResponse<AccessToken> reissue(
            @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        Token token = authFacade.reissueToken(refreshToken);
        sendCookie(response, token.refreshToken());

        return new SuccessResponse<>(new AccessToken(token.accessToken()));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(
            @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken
    ) {
        tokenFacade.logout(refreshToken);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/account")
    public void deleteAccount(
            @RequestAttribute("memberId") Long memberId
    ) {
        authFacade.deleteAccount(memberId);
    }

    @PostMapping("/test/token")
    public SuccessResponse<AccessToken> testToken() {
        AccessToken response = authFacade.issueTokenForTest();

        return new SuccessResponse<>(response);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("REFRESH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void sendCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("REFRESH_TOKEN", accessToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)(jwtProperties.refreshTokenExpireTimeInHours() * HOURS_IN_MINIUTES));
        response.addCookie(cookie);
    }
}
