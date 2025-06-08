package com.dubu.backend.todo.application;

import com.dubu.backend.todo.application.api.MemberApi;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.domain.factory.TodoFactory;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.repository.CategoryRepository;
import com.dubu.backend.todo.domain.repository.ScheduleRepository;
import com.dubu.backend.todo.domain.repository.TodoRepository;
import com.dubu.backend.todo.domain.dto.TodoSearchCond;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.dubu.backend.todo.application.TodoHelper.*;
import static com.dubu.backend.todo.domain.policy.RecommendTodoSelectionPolicy.*;


@Service
@RequiredArgsConstructor
@Transactional
public class TodoInitFacade {
    private final TodoFactory todoFactory;

    private final CategoryRepository categoryRepository;
    private final ScheduleRepository scheduleRepository;
    private final TodoRepository todoRepository;

    private final MemberApi memberApi;

    public void initTodayTodos(Long memberId){
        List<String> categoryNames = memberApi.getMemberCategories(memberId);
        Schedule schedule = scheduleRepository.save(Schedule.of(memberId, LocalDate.now()));

        List<Category> categories = categoryRepository.findByNameIn(categoryNames);
        List<Long> allRecommendTodoIds = todoRepository.findTodoIds(TodoSearchCond.of(TodoType.RECOMMEND, categories));

        List<Long> selectedRecommendTodoIds = selectForTodayTodoInitialization(allRecommendTodoIds);

        List<Todo> todayTodos = todoRepository.findByIdIn(selectedRecommendTodoIds).stream()
                .map(t -> todoFactory.createScheduleTodoFromParent(memberId, schedule, t))
                .toList();

        todoRepository.saveAll(todayTodos);
    }

    public void initPathTodos(Long memberId, Long subPathId){
        Schedule schedule = findExistingTodaySchedule(scheduleRepository, memberId);

        List<Todo> todayTodos = todoRepository.findByScheduleId(schedule.getId());

        List<Todo> pathTodos = todayTodos.stream()
                .map(t -> todoFactory.createPathTodoFromOrigin(memberId, subPathId, t))
                .toList();
        todoRepository.saveAll(pathTodos);
    }
}
