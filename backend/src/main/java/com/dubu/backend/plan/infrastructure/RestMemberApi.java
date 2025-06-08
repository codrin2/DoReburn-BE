package com.dubu.backend.plan.infrastructure;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.core.interceptor.context.TokenContext;
import com.dubu.backend.plan.application.api.MemberApi;

import com.dubu.backend.plan.domain.Member;
import com.dubu.backend.plan.infrastructure.response.MemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Objects;

@Component("plan.restMemberApi")
@Slf4j
public class RestMemberApi implements MemberApi {
    private final RestClient restClient;

    public RestMemberApi(@Qualifier("plan.restClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Member getMember(Long memberId) {


        SuccessResponse<MemberResponse> response =  restClient.get()
                .uri(uriBuilder -> {
                    URI uri = uriBuilder.path("/internal/members").build();
                    log.info("uri: {}", uri.getPath());
                    return uri;
                })
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<>(){});

        return mapToMember(Objects.requireNonNull(response).data());
    }

    private Member mapToMember(MemberResponse response){
        return Member.of(response.memberId(), response.status(), response.createdAt());
    }
}
