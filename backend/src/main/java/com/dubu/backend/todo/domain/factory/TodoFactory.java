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

    public Todo createScheduleTodo(Member member, Schedule schedule, String title, Category category, TodoDifficulty difficulty, String memo){
        todoValidationService.validateScheduleTodoCount(schedule);

        return Todo.builder()
                .type(SCHEDULED)
                .memberId(member.getId())
                .scheduleId(schedule.getId())
                .category(category)
                .title(title)
                .difficulty(difficulty)
                .memo(memo)
                .build();
    }

    public Todo createScheduleTodoFromParent(Member member, Schedule schedule, Todo parentTodo){
        todoValidationService.validateDuplicateScheduleTodoFromParent(schedule, parentTodo);
        todoValidationService.validateScheduleTodoCount(schedule);

        return Todo.builder()
                .type(SCHEDULED)
                .memberId(member.getId())
                .scheduleId(schedule.getId())
                .parentInfo(new ParentInfo(parentTodo.getId(), parentTodo.getVersion()))
                .category(parentTodo.getCategory())
                .title(parentTodo.getTitle())
                .difficulty(parentTodo.getDifficulty())
                .memo(parentTodo.getMemo())
                .build();
    }

    public Todo createFavoriteTodo(Member member, String title, Category category, TodoDifficulty difficulty, String memo){
        return Todo.builder()
                .type(FAVORITE)
                .memberId(member.getId())
                .category(category)
                .title(title)
                .difficulty(difficulty)
                .memo(memo)
                .build();
    }

    public Todo createFavoriteTodoFromParent(Member member, Todo parentTodo){
        todoValidationService.validateDuplicateFavoriteTodoFromParent(member, parentTodo);

        return Todo.builder()
                .type(FAVORITE)
                .memberId(member.getId())
                .parentInfo(new ParentInfo(parentTodo.getId(), parentTodo.getVersion()))
                .category(parentTodo.getCategory())
                .title(parentTodo.getTitle())
                .difficulty(parentTodo.getDifficulty())
                .memo(parentTodo.getMemo())
                .build();
    }

    public Todo createPathTodo(Member member, Long subPathId, String title, Category category, TodoDifficulty difficulty, String memo){
        todoValidationService.validatePathTodoCount(subPathId);

        return Todo.builder()
                .type(IN_PROGRESS)
                .memberId(member.getId())
                .subPathId(subPathId)
                .category(category)
                .title(title)
                .difficulty(difficulty)
                .memo(memo)
                .build();
    }

    public Todo createPathTodoFromParent(Member member, Long subPathId, Todo parentTodo){
        todoValidationService.validateDuplicatePathTodoFromParent(subPathId, parentTodo);

        return Todo.builder()
                .type(IN_PROGRESS)
                .memberId(member.getId())
                .subPathId(subPathId)
                .parentInfo(new ParentInfo(parentTodo.getId(), parentTodo.getVersion()))
                .category(parentTodo.getCategory())
                .title(parentTodo.getTitle())
                .difficulty(parentTodo.getDifficulty())
                .memo(parentTodo.getMemo())
                .build();
    }

    public Todo createScheduleTodoFromOrigin(Member member, Schedule schedule, Todo originTodo) {
        return Todo.builder()
                .type(SCHEDULED)
                .memberId(member.getId())
                .scheduleId(schedule.getId())
                .parentInfo(originTodo.getParentInfo())
                .title(originTodo.getTitle())
                .category(originTodo.getCategory())
                .difficulty(originTodo.getDifficulty())
                .memo(originTodo.getMemo())
                .build();
    }

    public Todo createPathTodoFromOrigin(Member member, Long subPathId, Todo originTodo) {
        return Todo.builder()
                .type(IN_PROGRESS)
                .memberId(member.getId())
                .subPathId(subPathId)
                .parentInfo(originTodo.getParentInfo())
                .title(originTodo.getTitle())
                .category(originTodo.getCategory())
                .difficulty(originTodo.getDifficulty())
                .memo(originTodo.getMemo())
                .build();
    }

}
