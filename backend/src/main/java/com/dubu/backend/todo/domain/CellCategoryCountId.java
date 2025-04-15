package com.dubu.backend.todo.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CellCategoryCountId implements Serializable {
    private String cellId;
    private Long categoryId;
}
