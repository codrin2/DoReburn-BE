package com.dubu.backend.auth.application;

import com.dubu.backend.auth.dto.TokenResponse;
import com.dubu.backend.auth.exception.*;
import com.dubu.backend.auth.infra.RedisTokenRepository;
import com.dubu.backend.auth.config.JwtConfig;
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
public class TokenService {
    public static final long HOURS_IN_MILLIS = 60 * 60 * 1000L;

    private final JwtConfig jwtConfig;
    private final JwtManager jwtManager;
    private final RedisTokenRepository redisTokenRepository;
    private long accessTokenTime;
    private long refreshTokenTime;

    @PostConstruct
    public void init() {
        this.accessTokenTime = jwtConfig.accessTokenExpireTimeInHours() * HOURS_IN_MILLIS;
        this.refreshTokenTime = jwtConfig.refreshTokenExpireTimeInHours() * HOURS_IN_MILLIS;
    }

    public TokenResponse issue(Long memberId) {
        String newAccessToken = jwtManager.createToken(memberId, accessTokenTime);
        String newRefreshToken = jwtManager.createToken(memberId, refreshTokenTime);

        redisTokenRepository.saveRefreshToken(memberId.toString(), newRefreshToken, refreshTokenTime);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public TokenResponse reissue(String oldRefreshToken) {
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

        TokenResponse tokenResponse = issue(Long.valueOf(memberId));

        return tokenResponse;
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