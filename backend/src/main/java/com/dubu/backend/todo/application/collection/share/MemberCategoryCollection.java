package com.dubu.backend.todo.application.collection.share;

import com.dubu.backend.todo.dto.response.MemberCategoryInfo;
import lombok.Getter;

import java.util.*;

@Getter
public class MemberCategoryCollection {
    private final Map<Long, List<String>> memberToCategories = new HashMap<>();
    private final Map<String, Integer> categoryMemberCount = new HashMap<>();

    public MemberCategoryCollection(List<MemberCategoryInfo> memberCategoryInfos) {
        memberCategoryInfos
                .forEach(info -> {
                    memberToCategories
                            .computeIfAbsent(info.memberId(), k -> new ArrayList<>())
                            .add(info.category());

                    categoryMemberCount.merge(info.category(), 1, Integer::sum);
                });
    }
}
