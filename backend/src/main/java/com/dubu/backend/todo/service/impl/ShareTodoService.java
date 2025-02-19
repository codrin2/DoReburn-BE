package com.dubu.backend.todo.service.impl;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.infra.repository.PathRepository;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.share.dto.response.ShareTodoInfo;
import com.dubu.backend.share.dto.response.SurroundingMemberInfo;
import com.dubu.backend.todo.entity.Schedule;
import com.dubu.backend.todo.entity.Todo;
import com.dubu.backend.todo.entity.TodoType;
import com.dubu.backend.todo.exception.SaveTodoNotFoundFromTargetParentException;
import com.dubu.backend.todo.exception.ScheduleNotFoundException;
import com.dubu.backend.todo.exception.TodoNotFoundException;
import com.dubu.backend.todo.repository.ScheduleRepository;
import com.dubu.backend.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShareTodoService {
    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;
    private final ScheduleRepository scheduleRepository;
    private final PlanRepository planRepository;
    private final PathRepository pathRepository;

    @Transactional(readOnly = true)
    public SurroundingMemberInfo findTodosOfSurroundMember(Long memberId, Long surroundingMemberId) {
        Member selfMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        Member surroundingMember = memberRepository.findById(surroundingMemberId).orElseThrow(() -> new MemberNotFoundException(surroundingMemberId));

        Status surroundingMemberStatus = surroundingMember.getStatus();

        // 회원의 상태가 ONBOARDING 이면 예외 발생
        if(surroundingMemberStatus.equals(Status.ONBOARDING)){
            throw new InvalidMemberStatusException(surroundingMemberStatus.name());
        }

        List<Todo> surroundMemberTodos = null;

        // 회원의 상태가 STOP 이면 회원의 오늘 할 일을 가져온다.
        if (surroundingMemberStatus.equals(Status.STOP)) {
            Schedule surroundMemberTodaySchedule = scheduleRepository.findLatestSchedule(surroundingMember, LocalDate.now()).orElseThrow(ScheduleNotFoundException::new);

            surroundMemberTodos = todoRepository.findTodosWithCategoryBySchedule(surroundMemberTodaySchedule);
        }
        // 회원의 상태가 MOVE 나 FEEDBACK 이면 최근 계획의 할 일을 가져온다.
        else {
            Plan latestPlan = planRepository.findTopByMemberIdOrderByCreatedAtDesc(surroundingMemberId).orElseThrow(PlanNotFoundException::new);

            List<Path> pathsOfLatestPlan = pathRepository.findByPlanAndType(latestPlan, TodoType.IN_PROGRESS);

            surroundMemberTodos = pathsOfLatestPlan.stream()
                    .flatMap(path -> path.getTodos().stream())
                    .toList();
        }

        List<Long> surroundMemberParentTodoIds = todoRepository.findParentTodoIdsByParentTodoAndMemberAndType(surroundMemberTodos, selfMember, TodoType.SAVE);
         return SurroundingMemberInfo.of(surroundingMember.getNickname(),
                 surroundMemberTodos.stream()
                         .map(todo ->
                                 ShareTodoInfo.of(todo.getId(),
                                         todo.getTitle(),
                                         todo.getCategory().getName(),
                                         surroundMemberParentTodoIds.contains(todo.getId())))
                         .toList()
         );
    }

    @Transactional
    public void removeTodoFromSurroundingMemberTodo(Long memberId, Long parentTodoId){
        Member selfMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        Todo parentTodo = todoRepository.findById(parentTodoId).orElseThrow(() -> new TodoNotFoundException(parentTodoId));

        Todo targetTodo = todoRepository.findByMemberAndParentTodoAndType(selfMember, parentTodo, TodoType.SAVE).orElseThrow(() -> new SaveTodoNotFoundFromTargetParentException(parentTodoId));

        todoRepository.delete(targetTodo);
    }
}