package com.dubu.backend.todo.application;

import com.dubu.backend.todo.api.dto.request.TodoRequestType;
import com.dubu.backend.todo.core.exception.InvalidMemberStatusException;
import com.dubu.backend.todo.domain.enums.MemberStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class TodoQueryPermissionValidator {
    private final Map<MemberStatus, Set<TodoRequestType>> accessibleRequestMap;

    public TodoQueryPermissionValidator() {
        this.accessibleRequestMap = Map.of(
                MemberStatus.STOP, Set.of(TodoRequestType.TODAY, TodoRequestType.TOMORROW, TodoRequestType.FAVORITE, TodoRequestType.RECOMMEND),
                MemberStatus.MOVE, Set.of(TodoRequestType.PATH, TodoRequestType.FAVORITE, TodoRequestType.RECOMMEND)
        );
    }

    public void validate(MemberStatus status, TodoRequestType type){
        if(!accessibleRequestMap.getOrDefault(status, Set.of()).contains(type)){
            throw new InvalidMemberStatusException(status.name());
        }
    }
}
