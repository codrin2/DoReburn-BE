package com.dubu.backend.todo.domain.policy;

import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.core.exception.TodoCountExceededException;

import org.springframework.stereotype.Component;

import static com.dubu.backend.todo.domain.enums.TodoType.SCHEDULED;

@Component
public class TodoCountValidationPolicy {
    private static final int MAX_SCHEDULE_TODO_COUNT = 5;
    private static final int MAX_PATH_TODO_COUNT = 10;

    public void validateScheduleTodoCount(Long count){
        if(count >= MAX_SCHEDULE_TODO_COUNT){
            throw new TodoCountExceededException(SCHEDULED.name(), MAX_SCHEDULE_TODO_COUNT);
        }
    }

    public void validatePathTodoCount(Long count){
        if(count >= MAX_PATH_TODO_COUNT){
            throw new TodoCountExceededException(TodoType.IN_PROGRESS.name(), MAX_PATH_TODO_COUNT);
        }
    }
}
