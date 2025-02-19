package com.dubu.backend.share.dto.response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
}
