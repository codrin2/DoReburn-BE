package com.dubu.backend.todo.application;

import com.dubu.backend.todo.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.todo.dto.response.ShareInfo;

public interface ShareService {
    ShareInfo findSurroundingMembersInfo(Long memberId, SurroundingMemberQueryRequest request);
}