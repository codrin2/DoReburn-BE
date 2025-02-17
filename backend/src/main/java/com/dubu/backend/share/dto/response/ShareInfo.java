package com.dubu.backend.share.dto.response;

import java.util.Comparator;
import java.util.List;

public record ShareInfo(List<MemberLocationInfo> memberLocations, List<CategoryInfo> categoryRank) {

    public static ShareInfo of(List<MemberLocationInfo> memberLocationInfos, List<CategoryInfo> categoryInfos){
        return new ShareInfo(
                memberLocationInfos,
                categoryInfos.stream()
                        .sorted(Comparator.comparing(CategoryInfo::count).reversed())
                        .limit(3)
                        .toList()
                );
    }

}
