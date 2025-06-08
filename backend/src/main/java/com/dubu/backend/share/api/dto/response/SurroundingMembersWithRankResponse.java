package com.dubu.backend.share.api.dto.response;

import com.dubu.backend.share.application.dto.SurroundingMembersWithRankResult;
import lombok.Builder;

import java.util.List;

@Builder
public record SurroundingMembersWithRankResponse(
    List<SurroundingMemberPointResponse> memberInfos,
    List<CategoryRankResponse> categoryRank
) {
    public static SurroundingMembersWithRankResponse from(SurroundingMembersWithRankResult result){
        return SurroundingMembersWithRankResponse.builder()
                .memberInfos(result.surroundingMembers().stream()
                        .map(sm -> new SurroundingMemberPointResponse(sm.memberId(), sm.x_coordinate(), sm.y_coordinate(), sm.categories()))
                        .toList())
                .categoryRank(result.categoryRanks().stream()
                        .map(cr -> new CategoryRankResponse(cr.category(), cr.count(), cr.rank()))
                        .toList()
                )
                .build();
    }

    private record SurroundingMemberPointResponse(
            Long memberId,
            Double x_coordinate,
            Double y_coordinate,
            List<String> categories
    ){}

    private record CategoryRankResponse(
            String category,
            Integer count,
            Integer rank
    ){}
}
