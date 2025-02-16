package com.dubu.backend.share.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주변 사용자 요청 dto")
public record SurroundingMemberQueryRequest(
        @Schema(description = "반경(km)", example = "3") Double radius,
        @Schema(description = "경도", example = "127.047328") Double x_coordinate,
        @Schema(description = "위도", example = "37.51723") Double y_coordinate
        ) {
}
