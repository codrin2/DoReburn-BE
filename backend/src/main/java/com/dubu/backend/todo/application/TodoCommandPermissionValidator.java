package com.dubu.backend.todo.application;

import com.dubu.backend.todo.api.dto.request.TodoRequestType;
import com.dubu.backend.todo.core.exception.InvalidMemberStatusException;
import com.dubu.backend.todo.domain.Member;
import com.dubu.backend.todo.domain.enums.MemberStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class TodoCommandPermissionValidator {
    private final Map<MemberStatus, Set<TodoRequestType>> accessibleCommandMap;

    public TodoCommandPermissionValidator() {
        this.accessibleCommandMap = Map.of(
                MemberStatus.STOP, Set.of(TodoRequestType.TODAY, TodoRequestType.TOMORROW, TodoRequestType.FAVORITE),
                MemberStatus.MOVE, Set.of(TodoRequestType.PATH)
        );
    }

    public void validate(Member member, TodoRequestType type){
        MemberStatus status = member.getStatus();
        if(!accessibleCommandMap.getOrDefault(status, Set.of()).contains(type)){
            throw new InvalidMemberStatusException(member.getStatus().name());
        }
    }
}
