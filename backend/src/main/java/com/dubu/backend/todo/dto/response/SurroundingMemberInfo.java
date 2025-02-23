package com.dubu.backend.todo.dto.response;

import java.util.List;

public record SurroundingMemberInfo(String nickname, List<ShareTodoInfo> todos) {
    public static SurroundingMemberInfo of(String nickname, List<ShareTodoInfo> shareTodoInfos) {
        return new SurroundingMemberInfo(nickname, shareTodoInfos);
    }
}
