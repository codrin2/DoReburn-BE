package com.dubu.backend.notification.application;

import com.dubu.backend.notification.application.response.MemberResponse;

public interface MemberApi {
    MemberResponse getMemberByToken(String token);
}