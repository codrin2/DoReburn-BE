package com.dubu.backend.member.domain.model;

import com.dubu.backend.core.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CellCategoryMemberCount extends BaseTimeEntity {

    @EmbeddedId
    private CellCategoryMemberCountId id;

    @Builder.Default
    @Column(nullable = false)
    private Integer count = 0;

    public void plusCount(int count){
        this.count += count;
    }

    public void minusCount(int count){
        this.count -= count;
    }
}
