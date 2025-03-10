package com.dubu.backend.todo.application.impl.today;

import com.dubu.backend.core.domain.PageResponse;
import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.core.exception.MemberCategoryNotFoundException;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
import com.dubu.backend.todo.domain.*;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.dto.common.Cursor;
import com.dubu.backend.todo.dto.common.TodoIdentifier;
import com.dubu.backend.todo.dto.request.RecommendTodoQueryRequest;
import com.dubu.backend.todo.dto.request.SaveTodoQueryRequest;
import com.dubu.backend.todo.dto.response.TodoInfo;
import com.dubu.backend.todo.dto.search.TodoSearchCond;
import com.dubu.backend.todo.exception.ScheduleNotFoundException;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.ScheduleRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import com.dubu.backend.todo.application.TargetTodoQueryService;
import com.dubu.backend.todo.application.support.TodoRandomSelector;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodayTodoQueryService implements TargetTodoQueryService {
    private final MemberRepository memberRepository;
    private final MemberCategoryRepository memberCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final ScheduleRepository scheduleRepository;

    private final TodoRandomSelector todoRandomSelector;

    @Override
    @Transactional
    public List<TodoInfo> findTargetTodos(TodoIdentifier identifier) {
        Member member = memberRepository.findById(identifier.memberId()).orElseThrow(() -> new MemberNotFoundException(identifier.memberId()));

        // 회원의 상태는 정지여야 한다.
        if(!member.getStatus().equals(Status.STOP)){
            throw new InvalidMemberStatusException(member.getStatus().name());
        }

        Schedule todaySchedule = scheduleRepository.findLatestSchedule(member, LocalDate.now()).orElseGet(() -> {
            Schedule newSchedule = Schedule.of(LocalDate.now(), member);
            List<Long> categoryIds = memberCategoryRepository.findCategoryIdsByMember(member);

            if(categoryIds.isEmpty()){
                throw new MemberCategoryNotFoundException(identifier.memberId());
            }
            List<Long> recommendTodoIds = todoRepository.findTodosWithCategoryByCategoryIdsAndType(categoryIds, TodoType.RECOMMEND);
            List<Long> selectedTodoIds = todoRandomSelector.selectTodos(3, recommendTodoIds);
            List<Todo> selectedTodos = todoRepository.findTodosWithCategoryByIds(selectedTodoIds);

            Schedule savedSchedule = scheduleRepository.save(newSchedule);

            selectedTodos
                    .forEach(t -> todoRepository.save(Todo.copyOf(t, member, savedSchedule)));

            return savedSchedule;
        });

        List<Todo> todos = todoRepository.findTodosWithCategoryBySchedule(todaySchedule);

        return todos.stream().map(TodoInfo::fromEntity).toList();
    }

    @Override
    public PageResponse<Long, List<TodoInfo>> findSaveTodos(TodoIdentifier identifier, Long cursor, SaveTodoQueryRequest request) {
        Member member = memberRepository.findById(identifier.memberId()).orElseThrow(() -> new MemberNotFoundException(identifier.memberId()));

        // 회원의 상태는 정지여야 한다.
        if(!member.getStatus().equals(Status.STOP)){
            throw new InvalidMemberStatusException(member.getStatus().name());
        }

        Schedule schedule = scheduleRepository.findLatestSchedule(member, LocalDate.now()).orElseThrow(ScheduleNotFoundException::new);

        Slice<Todo> todoSlice = todoRepository.findTodosUsingSingleCursor(cursor,
                TodoSearchCond.builder()
                        .member(member)
                        .type(TodoType.SAVE)
                        .build(),
                PageRequest.ofSize(request.size()));

        List<Todo> saveTodos = todoSlice.getContent();

        // 오늘 할 일 의 부모 할 일이 즐겨찾기 할 일이거나 같은 부모 할 일을 가진 경우 hasChild = true
        List<Long> todayParentTodoIds = todoRepository.findParentTodoIdsByScheduleAndParentTodoNotNull(schedule);
        HashSet<Long> todayParentTodoIdSet = new HashSet<>(todayParentTodoIds);
        List<TodoInfo> todoInfos = saveTodos.stream()
                .map(st -> {
                    boolean hasChild = todayParentTodoIdSet.contains(st.getId())
                            || (st.getParentTodo() != null && todayParentTodoIdSet.contains(st.getParentTodo().getId()));
                    return TodoInfo.fromEntity(hasChild, st);
                }).toList();

        if(todoInfos.isEmpty()){
            return new PageResponse<>(todoSlice.hasNext(), null, todoInfos);
        }

        return new PageResponse<>(todoSlice.hasNext(), todoInfos.get(todoInfos.size() - 1).todoId(), todoInfos);
    }

    @Override
    public List<TodoInfo> findPersonalizedRecommendTodos(TodoIdentifier identifier) {
        Member member = memberRepository.findById(identifier.memberId()).orElseThrow(() -> new MemberNotFoundException(identifier.memberId()));

        // 회원의 상태는 정지여야 한다.
        if(!member.getStatus().equals(Status.STOP)){
            throw new InvalidMemberStatusException(member.getStatus().name());
        }

        Schedule schedule = scheduleRepository.findLatestSchedule(member, LocalDate.now()).orElseThrow(ScheduleNotFoundException::new);

        // 회원의 카테고리 정보에 해당하는 추천 할 일을 가져온다.
        List<Long> categoryIds = memberCategoryRepository.findCategoryIdsByMember(member);
        List<Long> recommendTodoIds = todoRepository.findTodosWithCategoryByCategoryIdsAndType(categoryIds, TodoType.RECOMMEND);

        List<Long> personalizedTodoIds = todoRandomSelector.selectTodos(5, recommendTodoIds);
        List<Todo> personalizedTodos = todoRepository.findAllById(personalizedTodoIds);


        // 오늘 할 일 의 부모 할 일이 추천 할 일이거나 즐겨찾기 할 일인데 즐겨찾기 할 일의 부모 할 일이 추천 할 일이라면 hasChild = true
        List<Long> todayParentTodoIds = todoRepository.findParentTodoIdsByScheduleAndParentTodoNotNull(schedule);
        List<Long> saveParentTodoIds = todoRepository.findParentTodoIdsByIdsAndTypeAndParentTodoNotNull(todayParentTodoIds, TodoType.SAVE);

        HashSet<Long> parentTodoIds = new HashSet<>(todayParentTodoIds);
        parentTodoIds.addAll(saveParentTodoIds);

        return personalizedTodos.stream()
                .map(pt -> {
                    boolean hasChild = parentTodoIds.contains(pt.getId());
                    return TodoInfo.fromEntity(hasChild, pt);
                }).toList();
    }

    @Override
    public PageResponse<Cursor, List<TodoInfo>> findAllRecommendTodos(TodoIdentifier identifier, Cursor cursor, RecommendTodoQueryRequest request) {
        Member member = memberRepository.findById(identifier.memberId()).orElseThrow(() -> new MemberNotFoundException(identifier.memberId()));

        // 회원의 상태는 정지여야 한다.
        if(!member.getStatus().equals(Status.STOP)){
            throw new InvalidMemberStatusException(member.getStatus().name());
        }

        Schedule schedule = scheduleRepository.findLatestSchedule(member, LocalDate.now()).orElseThrow(ScheduleNotFoundException::new);

        List<Category> categories = null;
        if(request.category() != null && !request.category().isEmpty()){
            categories = categoryRepository.findCategoriesByName(request.category());
        }

        List<TodoDifficulty> difficulties = null;
        if(request.difficulty() != null && !request.difficulty().isEmpty()){
            difficulties = request.difficulty().stream().map(TodoDifficulty::valueOf).toList();
        }

        Slice<Todo> todoSlice = todoRepository.findTodosUsingCompositeCursor(cursor,
                TodoSearchCond.builder()
                        .type(TodoType.RECOMMEND)
                        .categories(categories)
                        .difficulties(difficulties)
                        .build(),
                PageRequest.ofSize(request.size()));

        List<Todo> recommendTodos = todoSlice.getContent();

        // 오늘 할 일 의 부모 할 일이 추천 할 일이거나 즐겨찾기 할 일인데 즐겨찾기 할 일의 부모 할 일이 추천 할 일이라면 hasChild = true
        List<Long> todayParentTodoIds = todoRepository.findParentTodoIdsByScheduleAndParentTodoNotNull(schedule);
        List<Long> saveParentTodoIds = todoRepository.findParentTodoIdsByIdsAndTypeAndParentTodoNotNull(todayParentTodoIds, TodoType.SAVE);

        HashSet<Long> parentTodoIds = new HashSet<>(todayParentTodoIds);
        parentTodoIds.addAll(saveParentTodoIds);

        List<TodoInfo> todoInfos = recommendTodos.stream()
                .map(pt -> {
                    boolean hasChild = parentTodoIds.contains(pt.getId());
                    return TodoInfo.fromEntity(hasChild, pt);
                }).toList();

        if(todoInfos.isEmpty()){
            return new PageResponse<>(todoSlice.hasNext(), null, todoInfos);
        }
        Todo lastTodo = recommendTodos.get(recommendTodos.size() - 1);
        return new PageResponse<>(todoSlice.hasNext(), Cursor.of(lastTodo.getCategory().getId(), lastTodo.getDifficulty(), lastTodo.getId()), todoInfos);
    }
}