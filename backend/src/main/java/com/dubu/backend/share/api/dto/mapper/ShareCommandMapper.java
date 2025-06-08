package com.dubu.backend.share.api.dto.mapper;

import com.dubu.backend.share.api.dto.request.SurroundingMemberFetchRequest;
import com.dubu.backend.share.application.dto.SurroundingMemberFetchCommand;

public class ShareCommandMapper {
    public static SurroundingMemberFetchCommand mapToSurroundingFetchCommand(SurroundingMemberFetchRequest request){
        return SurroundingMemberFetchCommand.builder()
                .x_coordinate(request.x_coordinate())
                .y_coordinate(request.y_coordinate())
                .radius(request.radius())
                .build();
    }
}

