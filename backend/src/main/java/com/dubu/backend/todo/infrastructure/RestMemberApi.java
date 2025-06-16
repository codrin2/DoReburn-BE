package com.dubu.backend.todo.infrastructure;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.core.interceptor.context.TokenContext;
import com.dubu.backend.todo.application.api.MemberApi;
import com.dubu.backend.todo.domain.enums.MemberStatus;
import com.dubu.backend.todo.infrastructure.dto.MemberStatusResponse;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Component("todo.restMemberApi")
public class RestMemberApi implements MemberApi {
    private final RestClient restClient;

    public RestMemberApi(@Qualifier("todo.restClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public MemberStatus getMemberStatus(Long memberId) {
        SuccessResponse<MemberStatusResponse> response = restClient.get()
                .uri("/members/status")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        MemberStatusResponse memberStatusResponse = Objects.requireNonNull(response).data();

        return MemberStatus.fromString(memberStatusResponse.status());
    }

    @Override
    public List<String> getMemberCategories(Long memberId) {
        SuccessResponse<List<String>> response = restClient.get()
                .uri("/members/category")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return Objects.requireNonNull(response).data();
    }
}
