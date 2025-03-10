package com.dubu.backend.member.application;

import com.dubu.backend.member.presentation.response.UserInfo;

public interface OauthApi {
    String getAccessToken(String code);
    UserInfo getOauthUser(String accessToken);
}