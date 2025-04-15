package com.dubu.backend.todo.application;

import com.dubu.backend.todo.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.todo.dto.request.CategoryRankRequest;
import com.dubu.backend.todo.dto.response.CategoryRankInfo;
import com.dubu.backend.todo.dto.response.ShareInfo;
import com.dubu.backend.todo.dto.response.SurroundingMemberLocationInfo;

import java.util.List;

public interface ShareService {
    ShareInfo findSurroundingMembersInfo(Long memberId, SurroundingMemberQueryRequest request);
    List<SurroundingMemberLocationInfo> findSurroundingTempMembers(Long memberId, SurroundingMemberQueryRequest request);
    List<CategoryRankInfo> findCategoryRank(Long memberId, CategoryRankRequest request);
}