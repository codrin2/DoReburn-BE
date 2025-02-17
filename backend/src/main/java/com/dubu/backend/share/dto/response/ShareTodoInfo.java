package com.dubu.backend.share.dto.response;

public record ShareTodoInfo(Long todoId, String title, String category, Boolean isSaved) {

    public static ShareTodoInfo of(Long todoId, String title, String category, Boolean isSaved){
        return new ShareTodoInfo(todoId, title, category, isSaved);
    }
}