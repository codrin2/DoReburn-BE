package com.dubu.backend.todo.domain.policy;

import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dubu.backend.todo.domain.enums.TodoDifficulty.*;

@Component
public class TodoDifficultyBasedPathTimePolicy {

    public static List<TodoDifficulty> determineDifficulty(int pathTime){
        if(pathTime < 10) return List.of(EASY);
        else if(pathTime < 20) return List.of(EASY, NORMAL);
        else if(pathTime < 30) return List.of(NORMAL, HARD);
        else return List.of(HARD);
    }
}
