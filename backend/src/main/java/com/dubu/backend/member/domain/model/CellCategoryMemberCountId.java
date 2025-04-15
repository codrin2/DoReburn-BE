package com.dubu.backend.member.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CellCategoryMemberCountId implements Serializable {
    private String cellId;
    private Long categoryId;

    public CellCategoryMemberCountId(String cellId, Long categoryId) {
        this.cellId = cellId;
        this.categoryId = categoryId;
    }
}
