package com.dubu.backend.statistic.service.collection;

import com.dubu.backend.todo.entity.Category;
import com.dubu.backend.todo.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CategoryTodoStatistics {
    private final Map<String, TimeCount> categoryTodoTimeCount;

    public CategoryTodoStatistics(List<Category> categories) {
        this.categoryTodoTimeCount = categories.stream().collect(Collectors.toMap(Category::getName, category -> new TimeCount(0, 0)));
    }

    public void recordDoneTodo(Todo todo){
        categoryTodoTimeCount.computeIfPresent(todo.getCategory().getName(), (k, tc) -> {
            tc.incrementCount();
            tc.incrementTime(todo.getSpentTime());
            return tc;
        });
    }

    public void countDoneTodo(Todo todo){
        categoryTodoTimeCount.computeIfPresent(todo.getCategory().getName(), (k, tc) -> {
           tc.incrementCount();
           return tc;
        });
    }

    @Getter
    @AllArgsConstructor
    public static class TimeCount{
        private int time;
        private int count;

        public void incrementTime(int time){
            this.time += time;
        }
        public void incrementCount(){
            this.count++;
        }
    }
}