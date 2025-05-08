package com.dubu.backend.todo.application.dto.request;

import lombok.Builder;

@Builder
public record TodoFetchCommand(Long subPathId) {
    public static TodoFetchCommand empty(){
        return TodoFetchCommand.builder()
                .build();
    }

    public static TodoFetchCommand of(Long subPathId){
        return TodoFetchCommand.builder()
                .subPathId(subPathId)
                .build();
    }
}
