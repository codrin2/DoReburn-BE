package com.dubu.backend.share.application.api;

import com.dubu.backend.share.domain.SurroundingMember;

public interface MemberApi {
    SurroundingMember getSurroundingMember(Long memberId);
}
