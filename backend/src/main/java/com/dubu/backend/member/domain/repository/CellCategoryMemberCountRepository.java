package com.dubu.backend.member.domain.repository;

public interface CellCategoryMemberCountRepository{
    void adjustCellCategoryMemberCount(String cellId, Long categoryId, int delta);
}
