package com.dubu.backend.member.api.request;

import java.util.List;

public record CellCategoryUpdateByPlanRequest(List<Long> beforeCategoryIds, List<Long> recentCategoryIds) {
}
