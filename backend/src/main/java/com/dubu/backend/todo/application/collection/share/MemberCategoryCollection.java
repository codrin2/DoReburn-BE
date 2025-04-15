package com.dubu.backend.todo.application.collection.share;

import com.dubu.backend.todo.dto.response.MemberCategoryInfo;
import lombok.Getter;

import java.util.*;

@Getter
public class MemberCategoryCollection {
    private final Map<Long, List<String>> memberCategoryMap = new HashMap<>();
    private final Map<String, Integer> categoryMemberCountMap = new HashMap<>();

    public MemberCategoryCollection(List<MemberCategoryInfo> memberCategoryInfos) {
        memberCategoryInfos
                .forEach(info -> {
                    memberCategoryMap
                            .computeIfAbsent(info.memberId(), k -> new ArrayList<>())
                            .add(info.category());

                    categoryMemberCountMap.merge(info.category(), 1, Integer::sum);
                });
    }
}
