package com.dubu.backend.todo.domain;

import com.dubu.backend.core.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CellCategoryCount extends BaseTimeEntity {
    @EmbeddedId
    private CellCategoryCountId id;

    @Builder.Default
    @Column(nullable = false)
    private Integer count = 0;
}
