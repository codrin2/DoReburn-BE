package com.dubu.backend.member.domain.repository;

import com.dubu.backend.member.domain.MemberLocation;

public interface MemberLocationRepository {
    void saveMemberLocation(Long memberId, MemberLocation memberLocation);
}