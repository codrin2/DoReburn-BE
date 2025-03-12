package com.dubu.backend.notification.infrastructure;

import com.dubu.backend.notification.application.MemberApi;
import com.dubu.backend.notification.application.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RestMemberApi implements MemberApi {
    private static final String MEMBER_INFO_ENDPOINT = "/members/info";

    @Qualifier("notification.memberRestClient")
    private final RestClient restClient;

    @Override
    public MemberResponse getMemberByToken(String token) {
        return restClient.get()
                .uri(MEMBER_INFO_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(MemberResponse.class);
    }
}