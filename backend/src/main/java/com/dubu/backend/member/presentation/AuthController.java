package com.dubu.backend.member.presentation;

import com.dubu.backend.member.application.AuthFacade;
import com.dubu.backend.member.core.JwtProperties;
import com.dubu.backend.member.presentation.response.AccessToken;
import com.dubu.backend.member.presentation.response.Token;
import com.dubu.backend.member.exception.MissingTokenInCookieException;
import com.dubu.backend.core.domain.SuccessResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthApi {
    public static final long HOURS_IN_MINIUTES = 60 * 60L;

    private final JwtProperties jwtProperties;
    private final AuthFacade authFacade;

    @PostMapping("/kakao-login")
    public SuccessResponse<AccessToken> kakaoCallback(
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
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null) {
            throw new MissingTokenInCookieException();
        }

        Token token = authFacade.reissueToken(refreshToken);
        sendCookie(response, token.refreshToken());

        return new SuccessResponse<>(new AccessToken(token.accessToken()));
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