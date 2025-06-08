package com.dubu.backend.plan.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Todo {
    private Long id;
    private String category;
    private String title;
    private String difficulty;
    private String memo;
    private Integer spentTime;
    private Boolean isCompleted;
    private Long subPathId;

    public void updateSpentTime(Integer spentTime){
        this.spentTime = spentTime;
    }
}
