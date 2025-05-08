package com.dubu.backend.plan.domain;

import com.dubu.backend.plan.domain.enums.TodoDifficulty;
import com.dubu.backend.plan.domain.enums.TodoType;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity(name = "PlanTodo")
@Table(name = "todo")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLRestriction("deleted = false")
public class Todo {
    @Id
    @Column(name = "todo_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TodoType type;

    private String title;

    @Enumerated(EnumType.ORDINAL)
    private TodoDifficulty difficulty;
    private String memo;

    private Integer spentTime;
    private Boolean isCompleted;

    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_path_id", nullable = false)
    private SubPath subPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public void updateSpentTime(int sectionTimePerTodo){
        this.spentTime = sectionTimePerTodo;
    }
    public void updateTodoType(TodoType type) { this.type = type;}
}
