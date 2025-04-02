package com.dubu.backend.todo.api;

import com.dubu.backend.member.core.Polling;
import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.todo.dto.request.CategoryRankRequest;
import com.dubu.backend.todo.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.todo.dto.response.CategoryRankInfo;
import com.dubu.backend.todo.dto.response.ShareInfo;
import com.dubu.backend.todo.dto.response.SurroundingMemberLocationInfo;
import com.dubu.backend.todo.dto.response.SurroundingMemberTodoInfo;
import com.dubu.backend.todo.application.ShareService;
import com.dubu.backend.todo.application.impl.share.ShareTodoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
public class ShareController implements ShareApi{
    private final ShareService shareService;
    private final ShareTodoService shareTodoService;

    @Polling
    @GetMapping("/members/surrounding")
    public SuccessResponse<ShareInfo> getSurroundingMembers(
            @RequestAttribute Long memberId,
            @ModelAttribute SurroundingMemberQueryRequest request
            ){
        return new SuccessResponse<>(shareService.findSurroundingMembersInfo(memberId, request));
    }

    @Polling
    @GetMapping("/members/location")
    public SuccessResponse<List<SurroundingMemberLocationInfo>> getSurroundingMemberLocations(
        @RequestAttribute Long memberId,
        @ModelAttribute SurroundingMemberQueryRequest request){

        return new SuccessResponse<>(shareService.findSurroundingTempMembers(memberId, request));
    }

    @Polling
    @GetMapping("/category-rank")
    public SuccessResponse<List<CategoryRankInfo>> getSurroundingMemberCategoryRank(
            @RequestAttribute Long memberId,
            @ModelAttribute CategoryRankRequest request){

        return new SuccessResponse<>(shareService.findCategoryRank(memberId, request));
    }



    @GetMapping("/members/todos")
    public SuccessResponse<SurroundingMemberTodoInfo> getTodosForSurroundingMember(
            @RequestAttribute Long memberId,
            @RequestParam Long surroundingMemberId
    ){
        return new SuccessResponse<SurroundingMemberTodoInfo>(shareTodoService.findTodosOfSurroundMember(memberId, surroundingMemberId));
    }

    @DeleteMapping("/todos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(
            @RequestAttribute Long memberId,
            @RequestParam Long surroundingMemberTodoId
    ){
        shareTodoService.removeTodoFromSurroundingMemberTodo(memberId, surroundingMemberTodoId);
    }

}