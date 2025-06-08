package com.dubu.backend.plan.application.api;

import com.dubu.backend.plan.domain.Member;

public interface MemberApi {
    Member getMember(Long memberId);
}
