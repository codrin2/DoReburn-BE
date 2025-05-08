package com.dubu.backend.todo.application;

import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.domain.factory.TodoFactory;
import com.dubu.backend.todo.domain.policy.RecommendTodoSelectionPolicy;
import com.dubu.backend.todo.domain.Member;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.repository.MemberRepository;
import com.dubu.backend.todo.domain.repository.ScheduleRepository;
import com.dubu.backend.todo.domain.repository.TodoRepository;
import com.dubu.backend.todo.domain.repository.dto.TodoSearchCond;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.dubu.backend.todo.application.TodoServiceHelper.*;
import static com.dubu.backend.todo.domain.policy.RecommendTodoSelectionPolicy.*;


@Service
@RequiredArgsConstructor
@Transactional
public class TodoInitFacade {
    private final TodoFactory todoFactory;

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final TodoRepository todoRepository;

    public void initTodayTodos(Long memberId){
        Member member = findExistingMember(memberRepository, memberId);

        Schedule schedule = scheduleRepository.save(Schedule.of(member.getId(), LocalDate.now()));

        List<Long> allRecommendTodoIds = todoRepository.findTodoIds(TodoSearchCond.of(TodoType.RECOMMEND, member.getCategories().stream().toList()));

        List<Long> selectedRecommendTodoIds = selectForTodayTodoInitialization(allRecommendTodoIds);

        List<Todo> todayTodos = todoRepository.findByIdIn(selectedRecommendTodoIds).stream()
                .map(t -> todoFactory.createScheduleTodoFromParent(member, schedule, t))
                .toList();

        todoRepository.saveAll(todayTodos);
    }

    public void initPathTodos(Long memberId, Long subPathId){
        Member member = findExistingMember(memberRepository, memberId);
        Schedule schedule = findExistingTodaySchedule(scheduleRepository, member);

        List<Todo> todayTodos = todoRepository.findByScheduleId(schedule.getId());

        List<Todo> pathTodos = todayTodos.stream()
                .map(t -> todoFactory.createPathTodoFromOrigin(member, subPathId, t))
                .toList();
        todoRepository.saveAll(pathTodos);
    }
}
