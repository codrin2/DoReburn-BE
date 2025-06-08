package com.dubu.backend.todo.infrastructure;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.core.interceptor.context.TokenContext;
import com.dubu.backend.todo.application.api.PlanApi;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Component("todo.restPlanApi")
public class RestPlanApi implements PlanApi {
    private final RestClient restClient;

    public RestPlanApi(@Qualifier("todo.restClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Long> getSubPathIdOfRecentPlans(List<Long> memberIds) {
        SuccessResponse<List<Long>> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/plans/recent/sub-paths")
                        .queryParam("memberIds", memberIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                        .build())
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return response != null ? response.data(): List.of();
    }
}
