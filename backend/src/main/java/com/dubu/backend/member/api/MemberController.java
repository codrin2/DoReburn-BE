package com.dubu.backend.member.api;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.member.api.request.MemberInfoUpdateRequest;
import com.dubu.backend.member.api.request.MemberOnboardingRequest;
import com.dubu.backend.member.api.request.MemberStatusUpdateRequest;
import com.dubu.backend.member.api.response.MemberInfoResponse;
import com.dubu.backend.member.api.response.MemberResponse;
import com.dubu.backend.member.api.response.MemberSavedAddressResponse;
import com.dubu.backend.member.api.response.MemberStatusResponse;
import com.dubu.backend.member.application.MemberCommandFacade;
import com.dubu.backend.member.application.MemberLocationFacade;
import com.dubu.backend.member.application.MemberQueryFacade;
import com.dubu.backend.member.core.Polling;
import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.MemberLocation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController implements MemberApi {
    private final MemberQueryFacade memberQueryFacade;
    private final MemberCommandFacade memberCommandFacade;
    private final MemberLocationFacade memberLocationFacade;

    @GetMapping("/info")
    public MemberResponse getMemberByToken(
            HttpServletRequest request
    ) {
        String bearerToken = request.getHeader("Authorization");
        String token = parseAccessToken(bearerToken);
        Member currentMember = memberQueryFacade.getMemberByToken(token);

        return MemberResponse.from(currentMember);
    }

    @GetMapping
    public SuccessResponse<MemberInfoResponse> getMemberInfo(
            @RequestAttribute("memberId") Long memberId
    ) {
        MemberInfoResponse response = memberQueryFacade.findMemberInfo(memberId);

        return new SuccessResponse<>(response);
    }

    @GetMapping("/status")
    public SuccessResponse<MemberStatusResponse> getMemberStatus(
            @RequestAttribute("memberId") Long memberId
    ) {
        MemberStatusResponse response = memberQueryFacade.findMemberStatus(memberId);

        return new SuccessResponse<>(response);
    }

    @GetMapping("/address")
    public SuccessResponse<MemberSavedAddressResponse> getMemberSavedAddress(
            @RequestAttribute("memberId") Long memberId
    ) {
        MemberSavedAddressResponse response = memberQueryFacade.findMemberSavedAddress(memberId);

        return new SuccessResponse<>(response);
    }

    @GetMapping("/category")
    public SuccessResponse<List<String>> getMemberCategory(
            @RequestAttribute("memberId") Long memberId
    ) {
        return new SuccessResponse<>(memberQueryFacade.findMemberCategory(memberId));
    }

    @ResponseStatus(NO_CONTENT)
    @PatchMapping("/onboarding")
    public void completeOnboarding(
            @RequestAttribute("memberId") Long memberId,
            @RequestBody MemberOnboardingRequest request
    ) {
        memberCommandFacade.completeOnboarding(memberId, request);
    }

    @PatchMapping
    public SuccessResponse<MemberInfoResponse> updateMemberInfo(
            @RequestAttribute("memberId") Long memberId,
            @RequestBody MemberInfoUpdateRequest request
    ) {
        MemberInfoResponse response = memberCommandFacade.updateMemberInfo(memberId, request);

        return new SuccessResponse<>(response);
    }

    @ResponseStatus(NO_CONTENT)
    @PatchMapping("/status")
    public void updateMemberStatus(
            @RequestAttribute("memberId") Long memberId,
            @RequestBody MemberStatusUpdateRequest request
    ) {
        memberCommandFacade.updateMemberStatus(memberId, request.status());
    }

    @Polling
    @ResponseStatus(NO_CONTENT)
    @PutMapping("/location")
    public void updateMemberLocation(
            @RequestAttribute("memberId") Long memberId,
            @Valid @RequestBody MemberLocation memberLocation
    ) {
        memberLocationFacade.updateMemberLocation(memberId, memberLocation);
    }

    private String parseAccessToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}