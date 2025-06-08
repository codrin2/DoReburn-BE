package com.dubu.backend.plan.domain;

import com.dubu.backend.core.domain.BaseTimeEntity;
import com.dubu.backend.plan.core.AggregateRoot;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AggregateRoot
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Plan extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SubPath> subPaths = new ArrayList<>();

    @OneToOne(mappedBy = "plan")
    private Feedback feedback;

    @Column(nullable = false, columnDefinition = "SMALLINT")
    private Integer totalTime;

    private boolean isCompleted;

    @Transient
    private List<Todo> todos = new ArrayList<>();

    public static Plan createPlan(Member member, Integer totalTime) {
        return Plan.builder()
                .memberId(member.getId())
                .totalTime(totalTime)
                .build();
    }

    public void updateIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public void assignTodos(List<Todo> todos){
        if(todos != null && !todos.isEmpty()) this.todos = List.copyOf(todos);
    }
}