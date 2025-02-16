package com.dubu.backend.share.controller;

import com.dubu.backend.global.domain.SuccessResponse;
import com.dubu.backend.share.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.share.dto.response.ShareInfo;
import com.dubu.backend.todo.controller.TodoApi;
import com.dubu.backend.todo.dto.response.TodoSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestAttribute;


@Tag(name = "Share API", description = "지도(공유) API")
public interface ShareApi {

    @Operation(summary = "주변 사용자 위치 정보 및 카테고리 랭킹 조회 API", description = "주변 사용자 위치 및 카테고리 랭킹을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "주변 사용자 위치 정보 및 카테고리 랭킹 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TodoSuccessResponse.class,
                                    description = "지도(공유) 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "일일 통계 데이터가 있을 경우",
                                            value = """
                                            {
                                                "data": {
                                                     "memberLocations": [
                                                         {
                                                             "memberId": 9,
                                                             "x_coordinate": 127.03299969434738,
                                                             "y_coordinate": 37.50300047180349
                                                         },
                                                         {
                                                             "memberId": 2,
                                                             "x_coordinate": 127.0429989695549,
                                                             "y_coordinate": 37.52600053160333
                                                         },
                                                         {
                                                             "memberId": 8,
                                                             "x_coordinate": 127.04900175333023,
                                                             "y_coordinate": 37.505000366798214
                                                         },
                                                         {
                                                             "memberId": 5,
                                                             "x_coordinate": 127.06299751996994,
                                                             "y_coordinate": 37.509998836924424
                                                         },
                                                         {
                                                             "memberId": 1,
                                                             "x_coordinate": 127.04732805490494,
                                                             "y_coordinate": 37.51723546583434
                                                         },
                                                         {
                                                             "memberId": 3,
                                                             "x_coordinate": 127.05499917268753,
                                                             "y_coordinate": 37.520000846619176
                                                         },
                                                         {
                                                             "memberId": 10,
                                                             "x_coordinate": 127.0370015501976,
                                                             "y_coordinate": 37.497000786819335
                                                         },
                                                         {
                                                             "memberId": 6,
                                                             "x_coordinate": 127.0370015501976,
                                                             "y_coordinate": 37.49900068181405
                                                         },
                                                         {
                                                             "memberId": 7,
                                                             "x_coordinate": 127.04799860715866,
                                                             "y_coordinate": 37.499999361950834
                                                         }
                                                     ],
                                                     "categoryRank": [
                                                         {
                                                             "category": "NEWS",
                                                             "count": 8
                                                         },
                                                         {
                                                             "category": "READING",
                                                             "count": 5
                                                         },
                                                         {
                                                             "category": "ENGLISH",
                                                             "count": 5
                                                         }
                                                     ]
                                                }
                                            }
                                            """
                                    )
                            }
                    )),
            @ApiResponse(responseCode = "404",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                    """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "회원 미존재 예시",
                                            value = """
                                            {
                                              "errorCode": "MEMBER_NOT_FOUND",
                                              "message": "회원을 찾을 수 없습니다. memberId : 9999"
                                            }
                                            """)
                            }
                    )
            ),
    })
    SuccessResponse<ShareInfo> getSurroundingMembers(
            @RequestAttribute Long memberId,
            @ModelAttribute SurroundingMemberQueryRequest request
    );
}
