package com.dubu.backend.todo.domain;

import com.dubu.backend.core.domain.BaseTimeEntity;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parent_id", "schedule_id"})
})
@SQLDelete(sql = "UPDATE todo SET deleted = true where todo_id = ?")
@SQLRestriction("deleted = false")
public class Todo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoType type;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "difficulty", nullable = false)
    private TodoDifficulty difficulty;

    @Column(length = 500)
    private String memo;

    @Column(name = "spent_time", columnDefinition = "MEDIUMINT")
    private Integer spentTime;

    @Column(columnDefinition = "TINYINT")
    private Boolean isCompleted;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "sub_path_id")
    private Long subPathId;

    @Column(name = "schedule_id")
    private Long scheduleId;

    @Embedded
    private ParentInfo parentInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder.Default
    private Long version = 0L;
    private boolean deleted;


    public void update(String title, Category category, TodoDifficulty difficulty, String memo){
        if(title != null) this.title = title;
        if(category != null) this.category = category;
        if(difficulty != null) this.difficulty = difficulty;
        if(memo != null) this.memo = memo;
        this.version = this.version + 1;
    }

    public void updateSubPathId(Long subPathId) {
        this.subPathId = subPathId;
    }

    public void updateTodoType(TodoType type){ this.type = type;}

    public void updateSpentTime(int time){ this.spentTime = time;}

    public void toggleCompletion(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public void detachParentTodo(String title, Category category, TodoDifficulty difficulty){
        if(title != null && category != null && difficulty != null)
            this.parentInfo = null;
    }
}
