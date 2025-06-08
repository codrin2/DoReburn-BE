package com.dubu.backend.share.infrastructure;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.core.interceptor.context.TokenContext;
import com.dubu.backend.share.application.api.TodoApi;
import com.dubu.backend.share.domain.RecentTodo;
import com.dubu.backend.share.infrastructure.dto.TodoResponse;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("share.restTodoApi")
public class RestTodoApi implements TodoApi {
    private final RestClient restClient;

    public RestTodoApi(@Qualifier(value = "share.restClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<RecentTodo> getRecentTodosOfMembers(List<Long> memberIds) {
        SuccessResponse<List<TodoResponse>> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/members/todos/recent")
                        .queryParam("memberIds", memberIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                        .build())
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<SuccessResponse<List<TodoResponse>>>() {
                });

        List<TodoResponse> todoResponses = Objects.requireNonNull(response).data();
        return map(todoResponses);
    }

    @Override
    public List<RecentTodo> getRecentTodos(Long memberId) {
        SuccessResponse<List<TodoResponse>> response = restClient.get()
                .uri(URI.create(String.format("/internal/members/%d/todos/recent", memberId)))
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<SuccessResponse<List<TodoResponse>>>() {
                });

        List<TodoResponse> todoResponses = Objects.requireNonNull(response).data();
        return map(todoResponses);
    }

    @Override
    public void deleteFromFavorites(Long memberId, Long todoId) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/todos/favorites")
                        .queryParam("sourceTodoId", todoId)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .toBodilessEntity();
    }

    private List<RecentTodo> map(List<TodoResponse> responses){
        return responses.stream()
                .map(r -> RecentTodo.of(r.todoId(), r.memberId(), r.title(), r.category(), r.isAdded()))
                .toList();
    }
}
