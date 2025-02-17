package com.dubu.backend.share.dto.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;
import java.util.Map;

public record MemberInfo(@JsonUnwrapped MemberLocationInfo memberLocationInfo, List<String> category) {
    public static List<MemberInfo> from(List<MemberLocationInfo> memberLocationInfos, Map<Long, List<String>> memberToCategories) {
        return memberLocationInfos.stream()
                .map(info -> new MemberInfo(info, memberToCategories.get(info.memberId())))
                .toList();
    }
}
