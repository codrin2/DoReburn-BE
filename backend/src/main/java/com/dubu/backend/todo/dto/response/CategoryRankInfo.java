package com.dubu.backend.todo.dto.response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public record CategoryRankInfo(String category, Integer rank, Integer count) {
    public static List<CategoryRankInfo> from(Map<String, Integer> categoryMemberCount){
        List<Map.Entry<String, Integer>> sortedEntries = categoryMemberCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .toList();

        List<CategoryRankInfo> result = new ArrayList<>();
        int currentRank = 1;
        Integer previousCount = null;

        int index = 0;
        for(Map.Entry<String, Integer> entry : sortedEntries){
            index++;
            if (previousCount != null && !previousCount.equals(entry.getValue())) {
                currentRank = index;
            }
            result.add(new CategoryRankInfo(entry.getKey(), currentRank, entry.getValue()));
            previousCount = entry.getValue();
        }

        return result;
    }

    public static List<CategoryRankInfo> from(List<CategoryCountInfo> categoryCountInfos) {
        List<CategoryCountInfo> sortedCategoryCountInfos = categoryCountInfos.stream()
                .sorted(Comparator.comparing(CategoryCountInfo::count).reversed())
                .toList();

        List<CategoryRankInfo> result = new ArrayList<>();
        int currentRank = 1;
        Integer previousCount = null;

        int index = 0;
        for(CategoryCountInfo categoryCountInfo : sortedCategoryCountInfos){
            index++;
            if (previousCount != null && !previousCount.equals(categoryCountInfo.count().intValue())) {
                currentRank = index;
            }
            result.add(new CategoryRankInfo(categoryCountInfo.category(), currentRank, categoryCountInfo.count().intValue()));
            previousCount = categoryCountInfo.count().intValue();
        }

        return result;
    }
}
