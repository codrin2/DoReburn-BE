package com.dubu.backend.plan.infrastructure;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.core.interceptor.context.TokenContext;
import com.dubu.backend.plan.application.api.TodoApi;
import com.dubu.backend.plan.domain.Todo;
import com.dubu.backend.plan.infrastructure.response.TodoResponse;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("plan.restTodoApi")
public class RestTodoApi implements TodoApi {
    private final RestClient restClient;

    public RestTodoApi(@Qualifier("plan.restClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Todo> getTodos(List<Long> subPathIds) {
        SuccessResponse<List<TodoResponse>> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/todos/path")
                        .queryParam("subPathIds", subPathIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        List<TodoResponse> todoResponses = Objects.requireNonNull(response).data();

        return mapToTodo(todoResponses);
    }

    @Override
    public List<Todo> getCompletedTodos(List<Long> subPathIds) {
        SuccessResponse<List<TodoResponse>> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/todos/path/completed")
                        .queryParam("subPathIds", subPathIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});


        List<TodoResponse> todoResponses = Objects.requireNonNull(response).data();

        return mapToTodo(todoResponses);
    }

    private List<Todo> mapToTodo(List<TodoResponse> responses){
        return responses.stream()
                .map(r ->
                        new Todo(r.todoId(), r.category(), r.title(), r.difficulty(), r.memo(),
                                r.spentTime(), r.isCompleted(), r.subPathId()))
                .toList();
    }
}
