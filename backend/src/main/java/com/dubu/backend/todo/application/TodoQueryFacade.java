package com.dubu.backend.todo.application;

import com.dubu.backend.todo.api.dto.request.EditPageType;
import com.dubu.backend.todo.application.dto.request.TodoFetchCommand;
import com.dubu.backend.todo.application.dto.request.TodoFetchPagedCommand;
import com.dubu.backend.todo.api.dto.request.TodoRequestType;
import com.dubu.backend.todo.application.dto.response.TodoPageResult;
import com.dubu.backend.todo.application.dto.response.TodoResult;
import com.dubu.backend.todo.core.dto.TodoCursor;
import com.dubu.backend.todo.core.exception.UnsupportedOperationForTodoRequestTypeException;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.Member;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.dto.TodoChildExistenceInfo;
import com.dubu.backend.todo.domain.dto.TodoInfo;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.domain.repository.*;
import com.dubu.backend.todo.domain.repository.dto.TodoSearchCond;
import com.dubu.backend.todo.domain.service.TodoChildExistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dubu.backend.todo.application.TodoServiceHelper.*;
import static com.dubu.backend.todo.application.dto.response.TodoResult.*;
import static com.dubu.backend.todo.domain.policy.RecommendTodoSelectionPolicy.selectForPersonalizedRecommend;
import static com.dubu.backend.todo.domain.policy.TodoDifficultyBasedPathTimePolicy.determineDifficulty;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoQueryFacade {
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final SubPathRepository subPathRepository;

    private final TodoQueryPermissionValidator todoQueryPermissionValidator;

    private final TodoChildExistenceService todoChildExistenceService;

    public List<TodoResult> findTodos(Long memberId, TodoRequestType requestType, EditPageType editType, TodoFetchCommand command){
        Member member = findExistingMember(memberRepository, memberId);
        todoQueryPermissionValidator.validate(member, requestType);

        List<Todo> todos = findTodos(member, requestType, editType, command);
        List<Long> todoIds = extractTodoIdsFromTodos(todos);

        if (todoChildExistenceService.needsChildTodoExistenceCheck(requestType, editType, todoIds)) {
            List<TodoChildExistenceInfo> todoChildExistenceInfos = findTodoChildExistenceInfos(member, command.subPathId(), editType, todoIds);
            return mapTodosToResult(todos, todoChildExistenceInfos);
        }
        return mapTodosToResult(todos, null);
    }

    public TodoPageResult findTodosPaged(Long memberId, TodoRequestType requestType, EditPageType editType, TodoFetchPagedCommand command){
        Member member = findExistingMember(memberRepository, memberId);
        todoQueryPermissionValidator.validate(member, requestType);

        Slice<TodoInfo> todoInfoSlice = findTodoInfosPaged(member, requestType, command);
        List<TodoInfo> todoInfos = todoInfoSlice.getContent();
        List<Long> todoIds = extractTodoIdsFromTodoInfos(todoInfos);

        TodoCursor nextCursor = todoInfoSlice.hasNext() ? getNextCursor(requestType, todoInfos.get(todoInfos.size() - 1)) : null;

        if (todoChildExistenceService.needsChildTodoExistenceCheck(requestType, editType, todoIds)) {
            List<TodoChildExistenceInfo> todoChildExistenceInfos = findTodoChildExistenceInfos(member, command.subPathId(), editType, todoIds);

            return TodoPageResult.of(
                    todoInfoSlice.hasNext(),
                    nextCursor,
                    mapTodoInfosToResult(todoInfos, todoChildExistenceInfos)
            );
        }
        return TodoPageResult.of(
                todoInfoSlice.hasNext(),
                nextCursor,
                mapTodoInfosToResult(todoInfos, null)
        );
    }

    private List<Todo> findTodos(Member member, TodoRequestType requestType, EditPageType editType, TodoFetchCommand command) {
        return switch(requestType){
            case TODAY -> {
                Schedule schedule = findExistingTodaySchedule(scheduleRepository, member);
                yield todoRepository.findByScheduleId(schedule.getId());
            }
            case TOMORROW -> {
                Schedule schedule = findExistingTomorrowSchedule(scheduleRepository, member);
                yield todoRepository.findByScheduleId(schedule.getId());
            }
            case PATH -> todoRepository.findBySubPathId(command.subPathId());
            case RECOMMEND -> {
                List<Category> categories = member.getCategories().stream().toList();
                List<Long> recommendTodoIds = findRecommendTodoIds(editType, categories, command.subPathId());

                yield todoRepository.findByIdIn(selectForPersonalizedRecommend(recommendTodoIds));
            }
            default -> throw new UnsupportedOperationForTodoRequestTypeException(requestType);
        };
    }

    private List<Long> findRecommendTodoIds(EditPageType editType, List<Category> categories, Long subPathId){
        if(editType.equals(EditPageType.PATH)) {
            List<TodoDifficulty> difficulties = determineDifficulty(subPathRepository.findSectionTimeOfSubPath(subPathId));
            return todoRepository.findTodoIds(TodoSearchCond.of(
                    TodoType.RECOMMEND,
                    categories,
                    difficulties
            ));
        }
        return todoRepository.findTodoIds(TodoSearchCond.of(
                TodoType.RECOMMEND,
                categories
        ));
    }

    private Slice<TodoInfo> findTodoInfosPaged(Member member, TodoRequestType type, TodoFetchPagedCommand command){
        return switch (type) {
            case FAVORITE -> todoRepository.findTodosPaged(command.cursor(), TodoSearchCond.of(member.getId(), TodoType.FAVORITE), Pageable.ofSize(command.size()));
            case RECOMMEND -> {
                List<Category> categories = categoryRepository.findByNameIn(command.categories());
                List<TodoDifficulty> difficulties = command.difficulties() != null ? command.difficulties().stream().map(TodoDifficulty::fromString).toList() : null;
                yield todoRepository.findTodosPaged(command.cursor(), TodoSearchCond.of(TodoType.RECOMMEND, categories, difficulties), Pageable.ofSize(command.size()));
            }
            default -> throw new UnsupportedOperationForTodoRequestTypeException(type);
        };
    }

    private TodoCursor getNextCursor(TodoRequestType type, TodoInfo todoInfo){
        return switch(type){
            case FAVORITE -> TodoCursor.from(TodoRequestType.FAVORITE, todoInfo);
            case RECOMMEND -> TodoCursor.from(TodoRequestType.RECOMMEND, todoInfo);
            default -> throw new UnsupportedOperationForTodoRequestTypeException(type);
        };
    }

    private List<TodoChildExistenceInfo> findTodoChildExistenceInfos(Member member, Long subPathId, EditPageType editType, List<Long> todoIds) {
        return switch(editType){
            case TODAY -> todoChildExistenceService.findChildTodoExistenceInTodayEditPage(member, todoIds);
            case TOMORROW -> todoChildExistenceService.findChildTodoExistenceInTomorrowEditPage(member, todoIds);
            case PATH -> todoChildExistenceService.findChildTodoExistenceInPathEditPage(member, subPathId, todoIds);
            case FAVORITE -> todoChildExistenceService.findChildTodoExistenceInFavoriteEditPage(member, todoIds);
        };
    }

    private List<Long> extractTodoIdsFromTodos(List<Todo> todos) {
        return todos.stream()
                .map(Todo::getId)
                .toList();
    }

    private List<Long> extractTodoIdsFromTodoInfos(List<TodoInfo> todoInfos){
        return todoInfos.stream()
                .map(TodoInfo::todoId)
                .toList();
    }
    private List<TodoResult> mapTodosToResult(List<Todo> todos, List<TodoChildExistenceInfo> todoChildExistenceInfos){
        if (todoChildExistenceInfos == null) {
            return todos.stream()
                    .map(TodoResult::from)
                    .toList();
        }

        Map<Long, Boolean> hasChildMap = todoChildExistenceInfos.stream()
                .collect(Collectors.toMap(TodoChildExistenceInfo::todoId, TodoChildExistenceInfo::hasChild));
        return todos.stream()
                .map(t -> from(t, hasChildMap.get(t.getId())))
                .toList();
    }

    private List<TodoResult> mapTodoInfosToResult(List<TodoInfo> todoInfos, List<TodoChildExistenceInfo> todoChildExistenceInfos) {
        if (todoChildExistenceInfos == null) {
            return todoInfos.stream()
                    .map(TodoResult::from)
                    .toList();
        }

        Map<Long, Boolean> hasChildMap = todoChildExistenceInfos.stream()
                .collect(Collectors.toMap(TodoChildExistenceInfo::todoId, TodoChildExistenceInfo::hasChild));

        return todoInfos.stream()
                .map(ti -> from(ti, hasChildMap.get(ti.todoId())))
                .toList();
    }
}
