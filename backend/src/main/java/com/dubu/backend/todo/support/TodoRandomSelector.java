package com.dubu.backend.todo.support;

import com.dubu.backend.todo.exception.NotEnoughRecommendedTodosException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TodoRandomSelector {
    public List<Long> selectTodos(int num, List<Long> todoIds) {
        if (num > todoIds.size()) {
            throw new NotEnoughRecommendedTodosException();
        }
        Collections.shuffle(todoIds);
        return todoIds.subList(0, num);
    }
}