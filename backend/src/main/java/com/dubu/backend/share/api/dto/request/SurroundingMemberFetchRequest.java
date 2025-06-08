package com.dubu.backend.share.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SurroundingMemberFetchRequest(
        @Schema(description = "회원 ID", example = "12345") Long memberId,
        @Schema(description = "경도 좌표", example = "127.031121") Double x_coordinate,
        @Schema(description = "위도 좌표", example = "37.514542") Double y_coordinate,
        @Schema(description = "반경", example = "3") Double radius
) {}
