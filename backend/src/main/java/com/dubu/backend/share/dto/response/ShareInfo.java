package com.dubu.backend.share.dto.response;

import java.util.List;

public record ShareInfo(List<MemberInfo> memberInfos, List<CategoryRankInfo> categoryRank) {

    public static ShareInfo of(List<MemberInfo> memberInfos, List<CategoryRankInfo> categoryRankInfos){
        return new ShareInfo(
                memberInfos,
                categoryRankInfos.size() <= 3? categoryRankInfos: categoryRankInfos.subList(0, 3)
                );
    }

}