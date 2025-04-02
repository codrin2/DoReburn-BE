package com.dubu.backend.todo.dto.response;

import java.util.List;

public record SurroundingMemberTodoInfo(String nickname, List<ShareTodoInfo> todos) {
    public static SurroundingMemberTodoInfo of(String nickname, List<ShareTodoInfo> shareTodoInfos) {
        return new SurroundingMemberTodoInfo(nickname, shareTodoInfos);
    }
}
