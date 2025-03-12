package com.dubu.backend.member.domain.enums;

import static java.util.Locale.ENGLISH;

public enum OauthProvider {
    KAKAO,
    ;
    public static OauthProvider fromName(String type) {
        return OauthProvider.valueOf(type.toUpperCase(ENGLISH));
    }
}
