package com.dubu.backend.share.controller;

import com.dubu.backend.global.anotation.Polling;
import com.dubu.backend.global.domain.SuccessResponse;
import com.dubu.backend.share.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.share.dto.response.ShareInfo;
import com.dubu.backend.share.dto.response.SurroundingMemberInfo;
import com.dubu.backend.share.service.ShareService;
import com.dubu.backend.todo.service.impl.ShareTodoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/members/todos")
    public SuccessResponse<SurroundingMemberInfo> getTodosForSurroundingMember(
            @RequestAttribute Long memberId,
            @RequestParam Long surroundingMemberId
    ){
        return new SuccessResponse<SurroundingMemberInfo>(shareTodoService.findTodosOfSurroundMember(memberId, surroundingMemberId));
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
