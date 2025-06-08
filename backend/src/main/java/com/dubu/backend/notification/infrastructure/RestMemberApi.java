package com.dubu.backend.notification.infrastructure;

import com.dubu.backend.notification.application.MemberApi;
import com.dubu.backend.notification.application.response.MemberResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("notification.RestMemberApi")
public class RestMemberApi implements MemberApi {
    private static final String MEMBER_INFO_ENDPOINT = "/members/info";

    private final RestClient restClient;

    public RestMemberApi(@Qualifier("notification.memberRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public MemberResponse getMemberByToken(String token) {
        return restClient.get()
                .uri(MEMBER_INFO_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(MemberResponse.class);
    }
}