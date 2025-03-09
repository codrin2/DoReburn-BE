package com.dubu.backend.todo.application.impl.share;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.infra.repository.PathRepository;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.todo.dto.response.ShareTodoInfo;
import com.dubu.backend.todo.dto.response.SurroundingMemberInfo;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.exception.SaveTodoNotFoundFromTargetParentException;
import com.dubu.backend.todo.exception.TodoNotFoundException;
import com.dubu.backend.todo.infra.repository.ScheduleRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Plan latestPlan = planRepository.findTopByMemberAndIsCompletedOrderByCreatedAtDesc(surroundingMember, true).orElseThrow(PlanNotFoundException::new);

        List<Path> pathsOfLatestPlan = pathRepository.findPathsByPlanAndIsCompleted(latestPlan, true);

        List<Todo> surroundMemberTodos = pathsOfLatestPlan.stream()
                .flatMap(path -> path.getTodos().stream())
                .toList();

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