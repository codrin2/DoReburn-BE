package com.dubu.backend.share.infrastructure;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.core.interceptor.context.TokenContext;
import com.dubu.backend.share.application.api.MemberApi;
import com.dubu.backend.share.domain.SurroundingMember;

import com.dubu.backend.share.infrastructure.dto.MemberNicknameResponse;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Objects;


@Component("share.restMemberApi")
public class RestMemberApi implements MemberApi {
    private final RestClient restClient;

    public RestMemberApi(@Qualifier("share.restClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public SurroundingMember getSurroundingMember(Long memberId) {
        SuccessResponse<MemberNicknameResponse> response = restClient.get()
                .uri("/internal/members/nickname")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TokenContext.getToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        MemberNicknameResponse memberNicknameResponse = Objects.requireNonNull(response).data();

        return map(memberId, memberNicknameResponse);
    }

    private SurroundingMember map(Long memberId, MemberNicknameResponse response){
        return SurroundingMember.of(memberId, response.nickname());
    }
}
