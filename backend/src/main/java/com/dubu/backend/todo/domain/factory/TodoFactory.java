package com.dubu.backend.todo.domain.factory;

import com.dubu.backend.todo.domain.*;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.service.TodoValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.dubu.backend.todo.domain.enums.TodoType.*;

@Component
@RequiredArgsConstructor
public class TodoFactory {
    private final TodoValidationService todoValidationService;

    public Todo createScheduleTodo(Long memberId, Schedule schedule, String title, Category category, TodoDifficulty difficulty, String memo){
        todoValidationService.validateScheduleTodoCount(schedule);

        return Todo.builder()
                .type(SCHEDULED)
                .memberId(memberId)
                .scheduleId(schedule.getId())
                .category(category)
                .title(title)
                .difficulty(difficulty)
                .memo(memo)
                .build();
    }

    public Todo createScheduleTodoFromParent(Long memberId, Schedule schedule, Todo parentTodo){
        todoValidationService.validateDuplicateScheduleTodoFromParent(schedule, parentTodo);
        todoValidationService.validateScheduleTodoCount(schedule);

        return Todo.builder()
                .type(SCHEDULED)
                .memberId(memberId)
                .scheduleId(schedule.getId())
                .parentInfo(new ParentInfo(parentTodo.getId(), parentTodo.getVersion()))
                .category(parentTodo.getCategory())
                .title(parentTodo.getTitle())
                .difficulty(parentTodo.getDifficulty())
                .memo(parentTodo.getMemo())
                .build();
    }

    public Todo createFavoriteTodo(Long memberId, String title, Category category, TodoDifficulty difficulty, String memo){
        return Todo.builder()
                .type(FAVORITE)
                .memberId(memberId)
                .category(category)
                .title(title)
                .difficulty(difficulty)
                .memo(memo)
                .build();
    }

    public Todo createFavoriteTodoFromParent(Long memberId, Todo parentTodo){
        todoValidationService.validateDuplicateFavoriteTodoFromParent(memberId, parentTodo);

        return Todo.builder()
                .type(FAVORITE)
                .memberId(memberId)
                .parentInfo(new ParentInfo(parentTodo.getId(), parentTodo.getVersion()))
                .category(parentTodo.getCategory())
                .title(parentTodo.getTitle())
                .difficulty(parentTodo.getDifficulty())
                .memo(parentTodo.getMemo())
                .build();
    }

    public Todo createPathTodo(Long memberId, Long subPathId, String title, Category category, TodoDifficulty difficulty, String memo){
        todoValidationService.validatePathTodoCount(subPathId);

        return Todo.builder()
                .type(IN_PROGRESS)
                .memberId(memberId)
                .subPathId(subPathId)
                .category(category)
                .title(title)
                .difficulty(difficulty)
                .memo(memo)
                .build();
    }

    public Todo createPathTodoFromParent(Long memberId, Long subPathId, Todo parentTodo){
        todoValidationService.validateDuplicatePathTodoFromParent(subPathId, parentTodo);

        return Todo.builder()
                .type(IN_PROGRESS)
                .memberId(memberId)
                .subPathId(subPathId)
                .parentInfo(new ParentInfo(parentTodo.getId(), parentTodo.getVersion()))
                .category(parentTodo.getCategory())
                .title(parentTodo.getTitle())
                .difficulty(parentTodo.getDifficulty())
                .memo(parentTodo.getMemo())
                .build();
    }

    public Todo createScheduleTodoFromOrigin(Long memberId, Schedule schedule, Todo originTodo) {
        return Todo.builder()
                .type(SCHEDULED)
                .memberId(memberId)
                .scheduleId(schedule.getId())
                .parentInfo(originTodo.getParentInfo())
                .title(originTodo.getTitle())
                .category(originTodo.getCategory())
                .difficulty(originTodo.getDifficulty())
                .memo(originTodo.getMemo())
                .build();
    }

    public Todo createPathTodoFromOrigin(Long memberId, Long subPathId, Todo originTodo) {
        return Todo.builder()
                .type(IN_PROGRESS)
                .memberId(memberId)
                .subPathId(subPathId)
                .parentInfo(originTodo.getParentInfo())
                .title(originTodo.getTitle())
                .category(originTodo.getCategory())
                .difficulty(originTodo.getDifficulty())
                .memo(originTodo.getMemo())
                .build();
    }

}
