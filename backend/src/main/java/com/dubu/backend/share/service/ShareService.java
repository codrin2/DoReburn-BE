package com.dubu.backend.share.service;

import com.dubu.backend.share.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.share.dto.response.ShareInfo;

public interface ShareService {
    ShareInfo findSurroundingMemberInfo(Long memberId, SurroundingMemberQueryRequest request);
}
