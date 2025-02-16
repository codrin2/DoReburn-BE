package com.dubu.backend.todo.service.impl;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.share.dto.response.ShareTodoInfo;
import com.dubu.backend.todo.entity.Schedule;
import com.dubu.backend.todo.entity.Todo;
import com.dubu.backend.todo.entity.TodoType;
import com.dubu.backend.todo.exception.ScheduleNotFoundException;
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

    @Transactional(readOnly = true)
    public List<ShareTodoInfo> findTodosOfSurroundMember(Long memberId, Long surroundingMemberId) {
        Member selfMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        Member surroundingMember = memberRepository.findById(surroundingMemberId).orElseThrow(() -> new MemberNotFoundException(surroundingMemberId));

        Schedule surroundMemberTodaySchedule = scheduleRepository.findLatestSchedule(surroundingMember, LocalDate.now()).orElseThrow(ScheduleNotFoundException::new);

        List<Todo> surroundMemberTodos = todoRepository.findTodosWithCategoryBySchedule(surroundMemberTodaySchedule);

        List<Long> surroundMemberParentTodoIds = todoRepository.findParentTodoIdsByParentTodoAndMemberAndType(surroundMemberTodos, selfMember, TodoType.SAVE);

        return surroundMemberTodos.stream()
                .map(todo ->
                        ShareTodoInfo.of(todo.getId(),
                                todo.getTitle(),
                                todo.getCategory().getName(),
                                surroundMemberParentTodoIds.contains(todo.getId())))
                .toList();
    }
}
