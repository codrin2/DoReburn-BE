package com.dubu.backend.todo.application;

import com.dubu.backend.todo.core.exception.MemberNotFoundException;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.Member;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.factory.TodoFactory;
import com.dubu.backend.todo.domain.repository.CategoryRepository;
import com.dubu.backend.todo.domain.repository.MemberRepository;
import com.dubu.backend.todo.domain.repository.ScheduleRepository;
import com.dubu.backend.todo.domain.repository.TodoRepository;
import com.dubu.backend.todo.core.exception.CategoryNotFoundException;
import com.dubu.backend.todo.core.exception.ScheduleNotFoundException;
import com.dubu.backend.todo.exception.TodoNotFoundException;

import java.time.LocalDate;
import java.util.List;

public class TodoServiceHelper {
    public static Member findExistingMember(MemberRepository memberRepository, Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    public static Category findExistingCategory(CategoryRepository categoryRepository, String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException(categoryName));
    }

    public static Todo findExistingTodo(TodoRepository todoRepository, Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException(todoId));
    }

    public static Schedule findExistingTodaySchedule(ScheduleRepository scheduleRepository, Member member){
        return scheduleRepository.findTopByMemberIdAndDateLessThanEqualOrderByDateDesc(member.getId(), LocalDate.now())
                .orElseThrow(ScheduleNotFoundException::new);
    }

    public static Schedule findExistingTomorrowSchedule(ScheduleRepository scheduleRepository, Member member) {
        return scheduleRepository.findTopByMemberIdAndDateLessThanEqualOrderByDateDesc(member.getId(), LocalDate.now().plusDays(1))
                .orElseThrow(ScheduleNotFoundException::new);
    }

    public static List<Todo> copyFromTodayTodos(TodoFactory todoFactory, List<Todo> originTodos, Member member, Schedule newSchedule) {
        return originTodos.stream()
                .map(ot -> todoFactory.createScheduleTodoFromOrigin(member, newSchedule, ot))
                .toList();
    }
}
