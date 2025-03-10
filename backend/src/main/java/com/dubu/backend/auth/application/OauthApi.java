package com.dubu.backend.auth.application;

import com.dubu.backend.auth.dto.KakaoUserInfo;

public interface OauthApi {
    String getAccessToken(String code);
    KakaoUserInfo getOauthUser(String accessToken);
}