package com.dubu.backend.todo.application;

import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.factory.TodoFactory;
import com.dubu.backend.todo.domain.repository.CategoryRepository;
import com.dubu.backend.todo.domain.repository.ScheduleRepository;
import com.dubu.backend.todo.domain.repository.TodoRepository;
import com.dubu.backend.todo.core.exception.CategoryNotFoundException;
import com.dubu.backend.todo.core.exception.ScheduleNotFoundException;
import com.dubu.backend.todo.exception.TodoNotFoundException;

import java.time.LocalDate;
import java.util.List;

public class TodoHelper {
    public static Category findExistingCategory(CategoryRepository categoryRepository, String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException(categoryName));
    }

    public static Todo findExistingTodo(TodoRepository todoRepository, Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException(todoId));
    }

    public static Schedule findExistingTodaySchedule(ScheduleRepository scheduleRepository, Long memberId){
        return scheduleRepository.findTopByMemberIdAndDateLessThanEqualOrderByDateDesc(memberId, LocalDate.now())
                .orElseThrow(ScheduleNotFoundException::new);
    }

    public static Schedule findExistingTomorrowSchedule(ScheduleRepository scheduleRepository, Long memberId) {
        return scheduleRepository.findTopByMemberIdAndDateLessThanEqualOrderByDateDesc(memberId, LocalDate.now().plusDays(1))
                .orElseThrow(ScheduleNotFoundException::new);
    }

    public static List<Todo> copyFromTodayTodos(TodoFactory todoFactory, List<Todo> originTodos, Long memberId, Schedule newSchedule) {
        return originTodos.stream()
                .map(ot -> todoFactory.createScheduleTodoFromOrigin(memberId, newSchedule, ot))
                .toList();
    }
}
