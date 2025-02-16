package com.dubu.backend.share.controller;

import com.dubu.backend.global.anotation.Polling;
import com.dubu.backend.global.domain.SuccessResponse;
import com.dubu.backend.share.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.share.dto.response.ShareInfo;
import com.dubu.backend.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
public class ShareController implements ShareApi{
    private final ShareService shareService;

    @Polling
    @GetMapping("/members/surrounding")
    public SuccessResponse<ShareInfo> getSurroundingMembers(
            @RequestAttribute Long memberId,
            @ModelAttribute SurroundingMemberQueryRequest request
            ){
        return new SuccessResponse<>(shareService.findSurroundingMemberInfo(memberId, request));
    }
}
