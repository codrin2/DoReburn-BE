package com.dubu.backend.member.application.api;

import com.dubu.backend.member.api.response.UserInfo;

public interface OauthApi {
    String getAccessToken(String code);
    UserInfo getOauthUser(String accessToken);
}