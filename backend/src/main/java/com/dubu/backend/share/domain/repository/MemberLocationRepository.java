package com.dubu.backend.share.domain.repository;

import com.dubu.backend.share.domain.SurroundingMember;

import java.util.List;

public interface MemberLocationRepository {
    List<SurroundingMember> findMemberLocations(Long memberId, double x_coordinate, double y_coordinate, double radius);
}
