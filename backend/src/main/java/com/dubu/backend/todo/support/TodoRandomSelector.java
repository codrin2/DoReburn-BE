package com.dubu.backend.todo.support;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Component
public class TodoRandomSelector {

    public Long selectOne(List<Long> todoIds){
        return todoIds.get(ThreadLocalRandom.current().nextInt(todoIds.size()));
    }

    public List<Long> selectTodos(int num, List<Long> todoIds){
        Collections.shuffle(todoIds);
        return todoIds.subList(0, num);
    }
}