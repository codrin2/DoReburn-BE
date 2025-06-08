package com.dubu.backend.todo.application;

import com.dubu.backend.todo.application.api.MemberApi;
import com.dubu.backend.todo.application.dto.request.TodoCompleteCommand;
import com.dubu.backend.todo.application.dto.request.TodoCreateCommand;
import com.dubu.backend.todo.application.dto.request.TodoCreateFromArchiveCommand;
import com.dubu.backend.todo.api.dto.request.TodoRequestType;
import com.dubu.backend.todo.application.dto.request.TodoUpdateCommand;
import com.dubu.backend.todo.application.dto.response.TodoResult;
import com.dubu.backend.todo.application.dto.response.TomorrowTodoResult;
import com.dubu.backend.todo.core.exception.UnsupportedOperationForTodoRequestTypeException;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.MemberStatus;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.domain.factory.TodoFactory;
import com.dubu.backend.todo.domain.repository.*;
import com.dubu.backend.todo.domain.service.ScheduleDateService;
import com.dubu.backend.todo.exception.TodoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.dubu.backend.todo.application.TodoHelper.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoCommandFacade {
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubPathRepository subPathRepository;

    private final TodoCommandPermissionValidator todoCommandPermissionValidator;
    private final ScheduleDateService scheduleDateService;
    private final TodoFactory todoFactory;

    private final MemberApi memberApi;

    public TodoResult createStandardTodo(Long memberId, TodoRequestType type, TodoCreateCommand command){
        MemberStatus status = memberApi.getMemberStatus(memberId);
        todoCommandPermissionValidator.validate(status, type);

        Todo todo = createTodo(memberId, type, command);
        Todo savedTodo = todoRepository.save(todo);

        return TodoResult.from(savedTodo);
    }

    public TodoResult createStandardTodoFromArchive(Long memberId, TodoRequestType type, TodoCreateFromArchiveCommand command) {
        MemberStatus status = memberApi.getMemberStatus(memberId);
        todoCommandPermissionValidator.validate(status, type);

        Todo todo = createTodoFromArchive(memberId, type, command);
        Todo savedTodo = todoRepository.save(todo);

        return TodoResult.from(savedTodo);
    }

    public TomorrowTodoResult createTomorrowTodo(Long memberId, TodoRequestType type, TodoCreateCommand command) {
        MemberStatus status = memberApi.getMemberStatus(memberId);
        todoCommandPermissionValidator.validate(status, type);

        Category category = findExistingCategory(categoryRepository, command.category());

        Schedule schedule = findExistingTomorrowSchedule(scheduleRepository, memberId);

        boolean hasBeenChanged = scheduleDateService.hasBeenChanged(schedule);
        if (!hasBeenChanged) {
            List<Todo> originTodos = todoRepository.findByScheduleId(schedule.getId());
            schedule = scheduleRepository.save(Schedule.of(memberId, LocalDate.now().plusDays(1)));


            List<Todo> newTodos = copyFromTodayTodos(todoFactory, originTodos, memberId, schedule);
            todoRepository.saveAll(newTodos);
        }

        Todo todo = todoFactory.createScheduleTodo(memberId, schedule,
                command.title(), category, TodoDifficulty.fromString(command.difficulty()), command.memo());
        Todo savedTodo = todoRepository.save(todo);

        return TomorrowTodoResult.of(!hasBeenChanged, TodoResult.from(savedTodo));
    }

    public TomorrowTodoResult createTomorrowTodoFromArchive(Long memberId, TodoRequestType type, TodoCreateFromArchiveCommand command) {
        MemberStatus status = memberApi.getMemberStatus(memberId);
        todoCommandPermissionValidator.validate(status, type);

        Todo parentTodo = findExistingTodo(todoRepository, command.archivedTodoId());

        Schedule schedule = findExistingTomorrowSchedule(scheduleRepository, memberId);

        boolean hasBeenChanged = scheduleDateService.hasBeenChanged(schedule);
        if(!hasBeenChanged){
            List<Todo> originTodos = todoRepository.findByScheduleId(schedule.getId());
            schedule = scheduleRepository.save(Schedule.of(memberId, LocalDate.now().plusDays(1)));


            List<Todo> newTodos = copyFromTodayTodos(todoFactory, originTodos, memberId, schedule);
            todoRepository.saveAll(newTodos);
        }

        Todo todo = todoFactory.createScheduleTodoFromOrigin(memberId, schedule, parentTodo);
        Todo savedTodo = todoRepository.save(todo);

        return TomorrowTodoResult.of(!hasBeenChanged, TodoResult.from(savedTodo));
    }


    public TodoResult updateStandardTodo(Long memberId, TodoRequestType type, TodoUpdateCommand command) {
        MemberStatus status = memberApi.getMemberStatus(memberId);
        todoCommandPermissionValidator.validate(status, type);
        Todo todo = findExistingTodo(todoRepository, command.todoId());

        Category category = command.category() != null ? findExistingCategory(categoryRepository, command.category()) : null;
        TodoDifficulty difficulty = command.difficulty() != null ? TodoDifficulty.fromString(command.difficulty()) : null;

        todo.detachParentTodo(command.title(), category, difficulty);
        todo.update(command.title(), category, difficulty, command.memo());

        return TodoResult.from(todo);
    }

    public TomorrowTodoResult updateTomorrowTodo(Long memberId, TodoRequestType type, TodoUpdateCommand command) {
        MemberStatus status = memberApi.getMemberStatus(memberId);
        todoCommandPermissionValidator.validate(status, type);
        Todo todo = findExistingTodo(todoRepository, command.todoId());
        Schedule schedule = findExistingTomorrowSchedule(scheduleRepository, memberId);

        boolean hasBeenChanged = scheduleDateService.hasBeenChanged(schedule);
        if(!hasBeenChanged){
            List<Todo> originTodos = todoRepository.findByScheduleId(schedule.getId());
            schedule = scheduleRepository.save(Schedule.of(memberId, LocalDate.now().plusDays(1)));

            List<Todo> newTodos = copyFromTodayTodos(todoFactory, originTodos, memberId, schedule);
            todoRepository.saveAll(newTodos);
            todo = findNewTargetTodo(todo, originTodos, newTodos);
        }

        Category category = command.category() != null ? findExistingCategory(categoryRepository, command.category()) : null;
        TodoDifficulty difficulty = command.difficulty() != null ? TodoDifficulty.fromString(command.difficulty()) : null;

        todo.detachParentTodo(command.title(), category, difficulty);
        todo.update(command.title(), category, difficulty, command.memo());

        return TomorrowTodoResult.of(!hasBeenChanged, TodoResult.from(todo));
    }

    public void completeTodoOnPlanEnd(TodoCompleteCommand command){
        List<Todo> planTodos = todoRepository.findByIdIn(command.planTodoIds());

        planTodos.forEach(todo -> todo.updateTodoType(TodoType.DONE));

        Map<Long, Integer> todoSpentTimeMap = command.todoSpentTimeMap();

        List<Todo> doneTodos = todoRepository.findByIdIn(todoSpentTimeMap.keySet().stream().toList());
        doneTodos.forEach(todo -> todo.updateSpentTime(todoSpentTimeMap.get(todo.getId())));
    }


    public void deleteStandardTodo(Long memberId, TodoRequestType type, Long todoId) {
        MemberStatus status = memberApi.getMemberStatus(memberId);
        todoCommandPermissionValidator.validate(status, type);
        Todo todo = findExistingTodo(todoRepository, todoId);

        todoRepository.delete(todo);
    }

    public TomorrowTodoResult deleteTomorrowTodo(Long memberId, TodoRequestType type, Long todoId) {
        MemberStatus status = memberApi.getMemberStatus(memberId);
        todoCommandPermissionValidator.validate(status, type);
        Todo todo = findExistingTodo(todoRepository, todoId);
        Schedule schedule = findExistingTomorrowSchedule(scheduleRepository, memberId);

        boolean hasBeenChanged = scheduleDateService.hasBeenChanged(schedule);
        if(!hasBeenChanged){
            List<Todo> originTodos = todoRepository.findByScheduleId(schedule.getId());
            schedule = scheduleRepository.save(Schedule.of(memberId, LocalDate.now().plusDays(1)));

            List<Todo> newTodos = copyFromTodayTodos(todoFactory, originTodos, memberId, schedule);
            todoRepository.saveAll(newTodos);
            todo = findNewTargetTodo(todo, originTodos, newTodos);
        }

        todoRepository.delete(todo);

        return TomorrowTodoResult.of(!hasBeenChanged, TodoResult.from(todo));
    }

    public void updateSubPathOfTodo(Long memberId, Long todoId, Long newSubPathId){
        Todo todo = findExistingTodo(todoRepository, todoId);
        todo.updateSubPathId(newSubPathId);
    }

    public void toggleCompletionOfTodo(Long memberId, Long todoId, boolean isCompleted) {
        Todo todo = findExistingTodo(todoRepository, todoId);
        todo.toggleCompletion(isCompleted);
    }

    public void deleteFavoritesTodoFromSource(Long memberId, Long sourceTodoId){
        Todo todo = todoRepository.findByMemberIdAndParentInfoParentId(memberId, sourceTodoId)
                .orElseThrow(TodoNotFoundException::new);

        todoRepository.delete(todo);
    }


    private Todo createTodo(Long memberId, TodoRequestType type, TodoCreateCommand command) {
        Category category = findExistingCategory(categoryRepository, command.category());
        TodoDifficulty difficulty = TodoDifficulty.fromString(command.difficulty());

        return switch(type) {
            case TODAY ->{
                Schedule schedule = findExistingTodaySchedule(scheduleRepository, memberId);
                yield todoFactory.createScheduleTodo(memberId, schedule, command.title(), category, difficulty, command.memo());
            }
            case PATH -> todoFactory.createPathTodo(memberId, command.subPathId(), command.title(), category, difficulty, command.memo());
            case FAVORITE -> todoFactory.createFavoriteTodo(memberId, command.title(), category, difficulty, command.memo());
            default -> throw new UnsupportedOperationForTodoRequestTypeException(type);
        };
    }

    private Todo createTodoFromArchive(Long memberId, TodoRequestType type, TodoCreateFromArchiveCommand command) {
        Todo parentTodo = findExistingTodo(todoRepository, command.archivedTodoId());

        return switch (type) {
            case TODAY ->{
                Schedule schedule = findExistingTodaySchedule(scheduleRepository, memberId);
                yield todoFactory.createScheduleTodoFromParent(memberId, schedule, parentTodo);
            }
            case PATH -> todoFactory.createPathTodoFromParent(memberId, command.subPathId(), parentTodo);
            case FAVORITE -> todoFactory.createFavoriteTodoFromParent(memberId, parentTodo);
            default -> throw new UnsupportedOperationForTodoRequestTypeException(type);
        };
    }

    private Todo findNewTargetTodo(Todo todo, List<Todo> originTodos, List<Todo> newTodos) {
        return newTodos.get(originTodos.indexOf(todo));
    }
}
