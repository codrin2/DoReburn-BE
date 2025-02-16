package com.dubu.backend.share.controller;

import com.dubu.backend.global.anotation.Polling;
import com.dubu.backend.global.domain.SuccessResponse;
import com.dubu.backend.share.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.share.dto.response.ShareInfo;
import com.dubu.backend.share.dto.response.ShareTodoInfo;
import com.dubu.backend.share.service.ShareService;
import com.dubu.backend.todo.service.impl.ShareTodoService;

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

    @GetMapping("/members/todos")
    public SuccessResponse<List<ShareTodoInfo>> getTodosForSurroundingMember(
            @RequestParam Long memberId,
            @RequestParam Long surroundingMemberId
    ){
        return new SuccessResponse<List<ShareTodoInfo>>(shareTodoService.findTodosOfSurroundMember(memberId, surroundingMemberId));
    }

    @DeleteMapping("/todo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(
            @RequestParam Long memberId,
            @RequestParam Long surroundingMemberTodoId
    ){
        shareTodoService.removeTodoFromSurroundingMemberTodo(memberId, surroundingMemberTodoId);
    }

}
