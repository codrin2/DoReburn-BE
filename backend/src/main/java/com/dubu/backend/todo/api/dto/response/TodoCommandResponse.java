package com.dubu.backend.todo.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record TodoCommandResponse<D>(
        @JsonInclude(JsonInclude.Include.NON_NULL) Boolean isTomorrowScheduleCreated,
        D data) {


    public static <D> TodoCommandResponse<D> of(Boolean isTomorrowScheduleCreated){
        return TodoCommandResponse.<D>builder()
                .isTomorrowScheduleCreated(isTomorrowScheduleCreated)
                .build();
    }

    public static <D> TodoCommandResponse<D> of(Boolean isTomorrowScheduleCreated, D data){
        return TodoCommandResponse.<D>builder()
                .isTomorrowScheduleCreated(isTomorrowScheduleCreated)
                .data(data)
                .build();
    }
}