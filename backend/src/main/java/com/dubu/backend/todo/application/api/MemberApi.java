package com.dubu.backend.todo.application.api;

import com.dubu.backend.todo.domain.enums.MemberStatus;

import java.util.List;

public interface MemberApi {
    MemberStatus getMemberStatus(Long memberId);
    List<String> getMemberCategories(Long memberId);
}
