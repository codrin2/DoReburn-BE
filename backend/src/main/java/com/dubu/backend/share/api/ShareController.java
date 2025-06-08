package com.dubu.backend.share.api;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.share.api.dto.mapper.ShareCommandMapper;
import com.dubu.backend.share.api.dto.request.SurroundingMemberFetchRequest;
import com.dubu.backend.share.api.dto.response.SurroundingMemberResponse;
import com.dubu.backend.share.api.dto.response.SurroundingMembersWithRankResponse;
import com.dubu.backend.share.application.ShareFacade;
import com.dubu.backend.share.application.dto.SurroundingMemberResult;
import com.dubu.backend.share.application.dto.SurroundingMembersWithRankResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
public class ShareController implements ShareApi{
    private final ShareFacade shareFacade;

    @GetMapping("/members/surrounding")
    public SuccessResponse<?> getSurroundingMembers(
        @RequestAttribute Long memberId,
        @ModelAttribute SurroundingMemberFetchRequest request
    ){
        SurroundingMembersWithRankResult result
                = shareFacade.findSurroundingMembersInfo(memberId, ShareCommandMapper.mapToSurroundingFetchCommand(request));

        return SuccessResponse.of(SurroundingMembersWithRankResponse.from(result));
    }

    @GetMapping("/members/todos")
    public SuccessResponse<?> getSurroundingMemberTodos(
            @RequestAttribute Long memberId,
            @RequestParam Long surroundingMemberId
    ){
        SurroundingMemberResult result = shareFacade.findSurroundingMemberInfo(memberId, surroundingMemberId);

        return SuccessResponse.of(SurroundingMemberResponse.from(result));
    }

    @DeleteMapping("/todos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFromFavorites(
            @RequestAttribute Long memberId,
            @RequestParam Long surroundingMemberTodoId
    ){
        shareFacade.deleteFromFavorites(memberId, surroundingMemberTodoId);
    }
}
