package com.dubu.backend.member.api;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.member.api.response.MemberDetailResponse;
import com.dubu.backend.member.api.response.MemberNicknameResponse;
import com.dubu.backend.member.application.MemberQueryFacade;
import com.dubu.backend.member.application.dto.MemberDetailResult;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/members")
@RequiredArgsConstructor
@Hidden
public class MemberInternalController {
    private final MemberQueryFacade memberQueryFacade;

    @GetMapping("")
    public SuccessResponse<MemberDetailResponse> getMemberDetail(
            @RequestAttribute Long memberId
    ){
        MemberDetailResult result = memberQueryFacade.findMember(memberId);
        return SuccessResponse.of(MemberDetailResponse.from(result));
    }

    @GetMapping("/nickname")
    public SuccessResponse<MemberNicknameResponse> getMemberNickname(
            @RequestAttribute Long memberId
    ){
        String nickname = memberQueryFacade.findMemberNickname(memberId);
        return SuccessResponse.of(MemberNicknameResponse.of(nickname));
    }
}
