package com.dubu.backend.todo.api;

import com.dubu.backend.core.domain.PageResponse;
import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.todo.api.dto.TodoCursorDto;
import com.dubu.backend.todo.api.dto.request.*;
import com.dubu.backend.todo.api.dto.response.TodoCommandResponse;
import com.dubu.backend.todo.api.dto.response.TodoResponse;
import com.dubu.backend.todo.application.TodoCommandFacade;
import com.dubu.backend.todo.application.TodoQueryFacade;
import com.dubu.backend.todo.application.dto.response.TodoItemResult;
import com.dubu.backend.todo.application.dto.response.TomorrowTodoResult;
import com.dubu.backend.todo.core.dto.TodoCursor;
import com.dubu.backend.todo.application.dto.request.TodoFetchCommand;
import com.dubu.backend.todo.application.dto.request.TodoFetchPagedCommand;
import com.dubu.backend.todo.application.dto.response.TodoPageResult;
import com.dubu.backend.todo.application.dto.response.TodoResult;

import com.dubu.backend.todo.core.exception.UnsupportedOperationForTodoRequestTypeException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dubu.backend.todo.api.dto.mapper.TodoCommandMapper.*;
import static com.dubu.backend.todo.api.dto.mapper.TodoResponseMapper.*;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController implements TodoApi {
    private final TodoQueryFacade todoQueryFacade;
    private final TodoCommandFacade todoCommandFacade;

    @Override
    @PostMapping("/{type}/manual")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoCommandResponse<?> postTodo(
            @RequestAttribute Long memberId,
            @PathVariable TodoRequestType type,
            @Nullable @RequestParam("pathId") Long pathId,
            @RequestBody TodoCreateRequest request
    ){
        return switch (type) {
            case TODAY, PATH, FAVORITE -> {
                TodoResult result = todoCommandFacade.createStandardTodo(memberId, type, toCommand(pathId, request));
                yield TodoCommandResponse.of(null, TodoResponse.from(result));
            }
            case TOMORROW -> {
                TomorrowTodoResult result = todoCommandFacade.createTomorrowTodo(memberId, type, toCommand(pathId, request));
                if (result.isTomorrowScheduleCreated()) {
                    List<TodoItemResult> todoItemResults = todoQueryFacade.findTodo(memberId, type, EditPageType.TOMORROW, TodoFetchCommand.empty());
                    yield TodoCommandResponse.of(true, mapTodoItemResultToResponse(todoItemResults));
                }
                yield TodoCommandResponse.of(false, TodoResponse.from(result.todoResult()));
            }
            default -> throw new UnsupportedOperationForTodoRequestTypeException(type);
        };
    }

    @PostMapping("/{type}/from-archived")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoCommandResponse<?> postTodoFromArchived(
            @RequestAttribute Long memberId,
            @PathVariable TodoRequestType type,
            @Nullable @RequestParam("pathId") Long pathId,
            @RequestBody TodoCreateFromArchiveRequest request
    ){
        return switch (type) {
            case TODAY, PATH, FAVORITE -> {
                TodoResult result = todoCommandFacade.createStandardTodoFromArchive(memberId, type, toCommand(pathId, request));
                yield TodoCommandResponse.of(null, TodoResponse.from(result));
            }
            case TOMORROW -> {
                TomorrowTodoResult result = todoCommandFacade.createTomorrowTodoFromArchive(memberId, type, toCommand(pathId, request));
                if (result.isTomorrowScheduleCreated()) {
                    List<TodoItemResult> todoItemResults = todoQueryFacade.findTodo(memberId, type, EditPageType.TOMORROW, TodoFetchCommand.empty());
                    yield TodoCommandResponse.of(true, mapTodoItemResultToResponse(todoItemResults));
                }
                yield TodoCommandResponse.of(false, TodoResponse.from(result.todoResult()));
            }
            default -> throw new UnsupportedOperationForTodoRequestTypeException(type);
        };
    }

    @PatchMapping("/{todoId}")
    public ResponseEntity<?> patchTodo(
            @RequestAttribute Long memberId,
            @PathVariable("todoId")Long todoId,
            @RequestParam("type") TodoRequestType type,
            @RequestBody TodoUpdateRequest request)
    {
        return switch (type) {
            case TODAY, PATH, FAVORITE -> {
                TodoResult result = todoCommandFacade.updateStandardTodo(memberId, type, toCommand(todoId, request));
                yield ResponseEntity.status(HttpStatus.OK)
                        .body(TodoCommandResponse.of(null, TodoResponse.from(result)));
            }
            case TOMORROW ->  {
                TomorrowTodoResult result = todoCommandFacade.updateTomorrowTodo(memberId, type, toCommand(todoId, request));
                if(result.isTomorrowScheduleCreated()){
                    List<TodoItemResult> todoItemResults = todoQueryFacade.findTodo(memberId, type, EditPageType.TOMORROW, TodoFetchCommand.empty());
                    yield ResponseEntity.status(HttpStatus.CREATED)
                            .body(TodoCommandResponse.of(true, mapTodoItemResultToResponse(todoItemResults)));
                }
                yield ResponseEntity.status(HttpStatus.OK)
                        .body(TodoCommandResponse.of(false, TodoResponse.from(result.todoResult())));
            }
            default -> throw new UnsupportedOperationForTodoRequestTypeException(type);
        };
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<?> deleteTodo(
            @RequestAttribute Long memberId,
            @PathVariable("todoId") Long todoId,
            @RequestParam("type") TodoRequestType type
    ){
        return switch (type) {
            case TODAY, PATH, FAVORITE -> {
                todoCommandFacade.deleteStandardTodo(memberId, type, todoId);
                yield ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            case TOMORROW -> {
                TomorrowTodoResult result = todoCommandFacade.deleteTomorrowTodo(memberId, type, todoId);
                if (result.isTomorrowScheduleCreated()) {
                    List<TodoItemResult> todoItemResults = todoQueryFacade.findTodo(memberId, type, EditPageType.TOMORROW, TodoFetchCommand.empty());
                    yield ResponseEntity.status(HttpStatus.CREATED)
                            .body(TodoCommandResponse.of(true, mapTodoItemResultToResponse(todoItemResults)));
                }
                yield ResponseEntity.status(HttpStatus.OK)
                        .body(TodoCommandResponse.of(false, null));
            }
            default -> throw new UnsupportedOperationForTodoRequestTypeException(type);
        };
    }

    @GetMapping("/{type}")
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<?> getTodos(
            @RequestAttribute Long memberId,
            @PathVariable("type") TodoRequestType type,
            @Nullable @RequestParam("pathId") Long subPathId
    ){
        List<TodoItemResult> results = todoQueryFacade.findTodo(memberId, type, EditPageType.fromString(type.name()), TodoFetchCommand.of(subPathId));

        return SuccessResponse.of(mapTodoItemResultToResponse(results));
    }

    @GetMapping("/recommend/personalized")
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<?> getRecommendTodos(
        @RequestAttribute Long memberId,
        @RequestParam("modifyType") String editType,
        @Nullable @RequestParam("pathId") Long subPathId
    ){
        List<TodoItemResult> results = todoQueryFacade.findTodo(
                memberId,
                TodoRequestType.RECOMMEND,
                EditPageType.fromString(editType),
                TodoFetchCommand.of(subPathId));

        return SuccessResponse.of(mapTodoItemResultToResponse(results));
    }

    @GetMapping("/favorite")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<?, ?> getFavoriteTodos(
            @RequestAttribute Long memberId,
            @RequestParam("modifyType") String editType,
            @Nullable @RequestParam("pathId") Long subPathId,
            @RequestParam(name = "cursor", required = false) Long todoId,
            @RequestParam Integer size
    ){
        TodoPageResult result = todoQueryFacade.findTodosPaged(
                memberId,
                TodoRequestType.FAVORITE,
                EditPageType.fromString(editType),
                TodoFetchPagedCommand.of(TodoCursor.of(todoId), subPathId, size));

        return PageResponse.of(result.hasNext(),
                TodoCursorDto.from(result.cursor()),
                mapTodoItemResultToResponse(result.todoItemResults()));
    }

    @GetMapping("/recommend/all")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<?, ?> getRecommendTodosPaged(
            @RequestAttribute Long memberId,
            @RequestParam("modifyType") String editType,
            @Nullable @RequestParam("pathId") Long subPathId,
            @Nullable @ModelAttribute TodoCursorDto cursor,
            @RequestParam Integer size,
            @ModelAttribute RecommendTodoFilterRequest request
    ){
        TodoPageResult result = todoQueryFacade.findTodosPaged(
                memberId,
                TodoRequestType.RECOMMEND,
                EditPageType.fromString(editType),
                TodoFetchPagedCommand.of(TodoCursor.from(cursor), subPathId, request.categories(), request.difficulties(), size)
        );

        return PageResponse.of(result.hasNext(),
                TodoCursorDto.from(result.cursor()),
                mapTodoItemResultToResponse(result.todoItemResults()));
    }

    @PatchMapping("/path")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchTodoPath(
            @RequestAttribute("memberId") Long memberId,
            @RequestParam Long todoId,
            @ModelAttribute TodoPathUpdateRequest request)
    {
        todoCommandFacade.updateSubPathOfTodo(memberId, todoId, request.newPathId());
    }

    @PatchMapping("/check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchTodoCompletionStatus(
            @RequestAttribute Long memberId,
            @RequestParam("todoId") Long todoId,
            @RequestBody TodoCompletionToggleRequest request
    ){
        todoCommandFacade.toggleCompletionOfTodo(memberId, todoId, request.isCompleted());
    }
}
