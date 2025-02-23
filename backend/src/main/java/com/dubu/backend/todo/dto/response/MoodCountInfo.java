package com.dubu.backend.todo.dto.response;

public record MoodCountInfo(String mood, Integer count) {
    public static MoodCountInfo of(String mood, Integer count){
        return new MoodCountInfo(mood, count);
    }
}
