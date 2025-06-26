package com.dubu.backend.member.application;

import com.dubu.backend.member.api.response.Token;
import com.dubu.backend.member.infrastructure.RedisTokenRepository;
import com.dubu.backend.member.core.JwtProperties;
import com.dubu.backend.member.core.exception.InvalidTokenHeaderException;
import com.dubu.backend.member.core.exception.TokenBlacklistedException;
import com.dubu.backend.member.core.exception.TokenMissingException;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenFacade {
    public static final long HOURS_IN_MILLIS = 60 * 60 * 1000L;

    private final JwtProperties jwtProperties;
    private final JwtManager jwtManager;
    private final RedisTokenRepository redisTokenRepository;
    private long accessTokenTime;
    private long refreshTokenTime;

    @PostConstruct
    public void init() {
        this.accessTokenTime = jwtProperties.accessTokenExpireTimeInHours() * HOURS_IN_MILLIS;
        this.refreshTokenTime = jwtProperties.refreshTokenExpireTimeInHours() * HOURS_IN_MILLIS;
    }

    public Token issue(Long memberId) {
        String newAccessToken = jwtManager.createToken(memberId, accessTokenTime);
        String newRefreshToken = jwtManager.createToken(memberId, refreshTokenTime);

        redisTokenRepository.saveRefreshToken(memberId.toString(), newRefreshToken, refreshTokenTime);

        return new Token(newAccessToken, newRefreshToken);
    }

    public Token reissue(String oldRefreshToken) {
        Claims claims = jwtManager.parseClaimsFromRefreshToken(oldRefreshToken);

        String jti = claims.getId();
        String memberId = claims.getSubject();

        if (redisTokenRepository.isBlacklisted(jti)) {
            String currentRefreshToken = redisTokenRepository.getRefreshToken(memberId);
            Date expiration = jwtManager.parseClaimsFromRefreshToken(currentRefreshToken).getExpiration();
            redisTokenRepository.addBlacklistToken(currentRefreshToken, getRemainingDuration(expiration));

            throw new TokenBlacklistedException();
        }

        redisTokenRepository.addBlacklistToken(jti, getRemainingDuration(claims.getExpiration()));

        Token token = issue(Long.valueOf(memberId));

        return token;
    }

    public void logout(String refreshToken) {
        try {
            TokenInfo tokenInfo = tokenManager.parseClaimsFromRefreshToken(refreshToken);
            String jti = tokenInfo.tokenId();
            Instant expiration = tokenInfo.expiration();

            redisTokenRepository.addBlacklistToken(jti, getRemainingDuration(expiration));
        } catch (Exception e) {
            log.warn("Invalid refresh token during logout. Token: {}", refreshToken, e);
        }
    }

    public Long validateToken(String accessToken) {
        Claims claims = jwtManager.parseClaims(accessToken);
        String memberId = claims.getSubject();

        return Long.parseLong(memberId);
    }

    public String resolveToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");
        if (jwtToken == null) {
            throw new TokenMissingException();
        }

        if (jwtToken.startsWith("Bearer ")) {
            return jwtToken.substring(7);
        } else {
            throw new InvalidTokenHeaderException();
        }
    }

    private Duration getRemainingDuration(Date expiration) {
        Instant now = Instant.now();
        Instant expirationTime = expiration.toInstant();

        return Duration.between(now, expirationTime);
    }
}