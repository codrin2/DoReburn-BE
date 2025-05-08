package com.dubu.backend.todo.domain.repository.dto;

import com.dubu.backend.todo.domain.Member;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.enums.TodoType;
import lombok.Builder;

@Builder
public record TodoChildExistenceCond(
        Long memberId,
        TodoType type,
        Long scheduleId,
        Long subPathId
) {

    public static TodoChildExistenceCond of(Member member){
        return TodoChildExistenceCond.builder()
                .memberId(member.getId())
                .type(TodoType.FAVORITE)
                .build();
    }

    public static TodoChildExistenceCond of(Schedule schedule){
        return TodoChildExistenceCond.builder()
                .scheduleId(schedule.getId())
                .build();
    }

    public static TodoChildExistenceCond of(Long subPathId){
        return TodoChildExistenceCond.builder()
                .subPathId(subPathId)
                .build();
    }
}
