package com.dubu.backend.todo.domain.service;

import com.dubu.backend.todo.api.dto.request.EditPageType;
import com.dubu.backend.todo.api.dto.request.TodoRequestType;
import com.dubu.backend.todo.domain.Member;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.dto.TodoChildExistenceInfo;
import com.dubu.backend.todo.domain.repository.ScheduleRepository;
import com.dubu.backend.todo.domain.repository.TodoRepository;
import com.dubu.backend.todo.domain.repository.dto.TodoChildExistenceCond;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dubu.backend.todo.application.TodoServiceHelper.*;

@Component
@RequiredArgsConstructor
public class TodoChildExistenceService {
    private final ScheduleRepository scheduleRepository;
    private final TodoRepository todoRepository;

    public boolean needsChildTodoExistenceCheck(TodoRequestType requestType, EditPageType editType, List<Long> todoIds) {
        if(todoIds.isEmpty()) return false;
        if(requestType.equals(TodoRequestType.TODAY) || requestType.equals(TodoRequestType.TOMORROW) | requestType.equals(TodoRequestType.PATH)) return false;
        if(requestType.equals(TodoRequestType.RECOMMEND)) return true;
        return !requestType.equals(TodoRequestType.FAVORITE) || !editType.equals(EditPageType.FAVORITE);
    }

    public List<TodoChildExistenceInfo> findChildTodoExistenceInTodayEditPage(Member member, List<Long> todoIds) {
        Schedule schedule = findExistingTodaySchedule(scheduleRepository, member);
        return todoRepository.findTodoChildExistence(todoIds, TodoChildExistenceCond.of(schedule));
    }

    public List<TodoChildExistenceInfo> findChildTodoExistenceInTomorrowEditPage(Member member, List<Long> todoIds) {
        Schedule schedule = findExistingTomorrowSchedule(scheduleRepository, member);
        return todoRepository.findTodoChildExistence(todoIds, TodoChildExistenceCond.of(schedule));
    }

    public List<TodoChildExistenceInfo> findChildTodoExistenceInPathEditPage(Member member, Long subPathId, List<Long> todoIds) {
        return todoRepository.findTodoChildExistence(todoIds, TodoChildExistenceCond.of(subPathId));
    }

    public List<TodoChildExistenceInfo> findChildTodoExistenceInFavoriteEditPage(Member member, List<Long> todoIds) {
        return todoRepository.findTodoChildExistence(todoIds, TodoChildExistenceCond.of(member));
    }
}
