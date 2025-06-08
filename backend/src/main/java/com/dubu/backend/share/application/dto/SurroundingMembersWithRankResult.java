package com.dubu.backend.share.application.dto;

import com.dubu.backend.share.domain.CategoryRank;
import com.dubu.backend.share.domain.SurroundingMember;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record SurroundingMembersWithRankResult(
    List<SurroundingMemberPointResult> surroundingMembers,
    List<CategoryRankResult> categoryRanks
) {
    public static SurroundingMembersWithRankResult from(List<SurroundingMember> surroundingMembers, List<CategoryRank> categoryRanks){
        List<CategoryRankResult> categoryRankResults = new ArrayList<>();
        int rank = 1;
        int priorCount = categoryRanks.get(0) != null ? categoryRanks.get(0).getCount() : 0;

        for(CategoryRank cr: categoryRanks){
            if(cr.getCount() != cr.getCount()){
                rank++;
                priorCount = cr.getCount();
            }
            categoryRankResults.add(new CategoryRankResult(cr.getCategory(), cr.getCount(), rank));
        }

        return SurroundingMembersWithRankResult.builder()
                .surroundingMembers(surroundingMembers.stream()
                        .map(sm -> new SurroundingMemberPointResult(sm.getMemberId(), sm.getX_coordinate(), sm.getY_coordinate(), sm.getRecentTodoCategories()))
                        .toList())
                .categoryRanks(categoryRankResults)
                .build();
    }

    public record SurroundingMemberPointResult(
            Long memberId,
            Double x_coordinate,
            Double y_coordinate,
            List<String> categories
    ){}

    public record CategoryRankResult(
            String category,
            Integer count,
            Integer rank
    ){}
}
