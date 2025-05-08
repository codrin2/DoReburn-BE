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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SubPath> subPaths = new ArrayList<>();

    @OneToOne(mappedBy = "plan")
    private Feedback feedback;

    @Column(nullable = false, columnDefinition = "SMALLINT")
    private Integer totalTime;

    private boolean isCompleted;

    @Transient
    private List<Todo> todos;

    public static Plan createPlan(Member member, Integer totalTime) {
        return Plan.builder()
                .member(member)
                .totalTime(totalTime)
                .build();
    }

    public void updateIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public  void defineTodos(List<Todo> todos){
        this.todos = List.copyOf(todos);
    }
}