package com.dubu.backend.todo.api;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.todo.api.dto.response.PathCompletedTodoResponse;
import com.dubu.backend.todo.api.dto.response.PathTodoResponse;
import com.dubu.backend.todo.api.dto.response.RecentTodoResponse;
import com.dubu.backend.todo.application.TodoCommandFacade;
import com.dubu.backend.todo.application.TodoQueryFacade;
import com.dubu.backend.todo.application.dto.response.TodoItemResult;
import com.dubu.backend.todo.application.dto.response.TodoResult;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
@Hidden
public class TodoInternalController {
    private final TodoQueryFacade todoQueryFacade;
    private final TodoCommandFacade todoCommandFacade;

    @GetMapping("/todos/path")
    public SuccessResponse<List<PathTodoResponse>> getPathTodos(
            @RequestParam List<Long> subPathIds
    ){
        List<TodoResult> result = todoQueryFacade.findPathTodos(subPathIds);

        return SuccessResponse.of(result.stream().map(PathTodoResponse::from).toList());
    }

    @GetMapping("/todos/path/completed")
    public SuccessResponse<List<PathCompletedTodoResponse>> getPathCompletedTodos(
            @RequestParam List<Long> subPathIds
    ){
        List<TodoResult> result = todoQueryFacade.findPathCompletedTodos(subPathIds);

        return SuccessResponse.of(result.stream().map(PathCompletedTodoResponse::from).toList());
    }

    @GetMapping("/members/todos/recent")
    public SuccessResponse<List<RecentTodoResponse>> getRecentTodosOfMembers(
            @RequestParam List<Long> memberIds
    ){
        List<TodoResult> todoResults = todoQueryFacade.findRecentTodos(memberIds);

        return SuccessResponse.of(todoResults.stream().map(RecentTodoResponse::from).toList());
    }

    @GetMapping("/members/{memberId}/todos/recent")
    public SuccessResponse<List<RecentTodoResponse>> getRecentTodos(
            @RequestAttribute("memberId") Long requestMemberId,
            @PathVariable("memberId") Long targetMemberId
    ){
        List<TodoItemResult> todoItemResults = todoQueryFacade.findRecentTodosWithChildCheck(requestMemberId, targetMemberId);

        return SuccessResponse.of(todoItemResults.stream().map(RecentTodoResponse::from).toList());
    }

    @DeleteMapping("/todos/favorites")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(
            @RequestAttribute Long memberId,
            @RequestParam Long sourceTodoId
    ){
        todoCommandFacade.deleteFavoritesTodoFromSource(memberId, sourceTodoId);
    }
}
