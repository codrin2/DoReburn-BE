package com.dubu.backend.todo.domain.service;

import com.dubu.backend.todo.domain.Member;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.domain.policy.TodoCountValidationPolicy;
import com.dubu.backend.todo.domain.repository.TodoRepository;
import com.dubu.backend.todo.core.exception.AlreadyAddedTodoFromArchiveException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodoValidationService {
    private final TodoRepository todoRepository;
    private final TodoCountValidationPolicy policy;

    public void validateScheduleTodoCount(Schedule schedule) {
        long count = todoRepository.countByScheduleId(schedule.getId());
        policy.validateScheduleTodoCount(count);
    }

    public void validatePathTodoCount(Long subPathId){
        long count = todoRepository.countBySubPathId(subPathId);
        policy.validatePathTodoCount(count);
    }

    public void validateDuplicateScheduleTodoFromParent(Schedule schedule, Todo parentTodo){
        if(todoRepository.existsByScheduleIdAndParentInfoParentId(schedule.getId(), parentTodo.getId())){
            throw new AlreadyAddedTodoFromArchiveException();
        }
    }

    public void validateDuplicateFavoriteTodoFromParent(Member member, Todo parentTodo){
        if(todoRepository.existsByMemberIdAndParentInfoParentIdAndType(member.getId(), parentTodo.getId(), TodoType.FAVORITE)){
            throw new AlreadyAddedTodoFromArchiveException();
        }
    }

    public void validateDuplicatePathTodoFromParent(Long subPathId, Todo parentTodo){
        if (todoRepository.existsBySubPathIdAndParentInfoParentId(subPathId, parentTodo.getId())) {
            throw new AlreadyAddedTodoFromArchiveException();
        }
    }
}
