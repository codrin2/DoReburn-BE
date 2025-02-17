package com.dubu.backend.share.controller;

import com.dubu.backend.global.domain.SuccessResponse;
import com.dubu.backend.share.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.share.dto.response.ShareInfo;
import com.dubu.backend.share.dto.response.SurroundingMemberInfo;
import com.dubu.backend.todo.controller.TodoApi;
import com.dubu.backend.todo.dto.response.TodoSuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Share API", description = "지도(공유) API")
public interface ShareApi {

    @Operation(summary = "주변 사용자 위치 정보 및 카테고리 랭킹 조회 API", description = "주변 사용자 위치 및 카테고리 랭킹을 조회한다. polling 을 통해 5분에 한 번씩 조회하거나 새로고침을 통해 조회힌다.")
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
                                            name = "주변 사용자 위치 정보 및 카테고리 랭킹 조회 성공",
                                            value = """
                                            {
                                              "data": {
                                                "memberInfos": [
                                                  {
                                                    "memberId": 9,
                                                    "x_coordinate": 127.03299969434738,
                                                    "y_coordinate": 37.50300047180349,
                                                    "category": [
                                                      "HOBBY",
                                                      "OTHERS"
                                                    ]
                                                  },
                                                  {
                                                    "memberId": 2,
                                                    "x_coordinate": 127.0429989695549,
                                                    "y_coordinate": 37.52600053160333,
                                                    "category": [
                                                      "NEWS"
                                                    ]
                                                  },
                                                  {
                                                    "memberId": 8,
                                                    "x_coordinate": 127.04900175333023,
                                                    "y_coordinate": 37.505000366798214,
                                                    "category": [
                                                      "READING",
                                                      "LANGUAGE",
                                                      "OTHERS"
                                                    ]
                                                  },
                                                  {
                                                    "memberId": 5,
                                                    "x_coordinate": 127.06299751996994,
                                                    "y_coordinate": 37.509998836924424,
                                                    "category": [
                                                      "LANGUAGE",
                                                      "NEWS",
                                                      "OTHERS"
                                                    ]
                                                  },
                                                  {
                                                    "memberId": 1,
                                                    "x_coordinate": 127.04732805490494,
                                                    "y_coordinate": 37.51723039639202,
                                                    "category": [
                                                      "READING"
                                                    ]
                                                  },
                                                  {
                                                    "memberId": 3,
                                                    "x_coordinate": 127.05499917268753,
                                                    "y_coordinate": 37.520000846619176,
                                                    "category": [
                                                      "READING",
                                                      "NEWS"
                                                    ]
                                                  },
                                                  {
                                                    "memberId": 10,
                                                    "x_coordinate": 127.0370015501976,
                                                    "y_coordinate": 37.497000786819335,
                                                    "category": [
                                                      "NEWS",
                                                      "HOBBY"
                                                    ]
                                                  },
                                                  {
                                                    "memberId": 6,
                                                    "x_coordinate": 127.0370015501976,
                                                    "y_coordinate": 37.49900068181405,
                                                    "category": [
                                                      "OTHERS",
                                                      "ENGLISH",
                                                      "READING",
                                                      "HOBBY"
                                                    ]
                                                  },
                                                  {
                                                    "memberId": 7,
                                                    "x_coordinate": 127.04799860715866,
                                                    "y_coordinate": 37.499999361950834,
                                                    "category": [
                                                      "READING",
                                                      "ENGLISH",
                                                      "HOBBY"
                                                    ]
                                                  }
                                                ],
                                                "categoryRank": [
                                                  {
                                                    "category": "READING",
                                                    "rank": 1,
                                                    "count": 5
                                                  },
                                                  {
                                                    "category": "OTHERS",
                                                    "rank": 2,
                                                    "count": 4
                                                  },
                                                  {
                                                    "category": "NEWS",
                                                    "rank": 2,
                                                    "count": 4
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
            )
    })
    SuccessResponse<ShareInfo> getSurroundingMembers(
            @RequestAttribute Long memberId,
            @ModelAttribute SurroundingMemberQueryRequest request
    );

    @Operation(summary = "주변 사용자 할 일 조회 API", description = "주변 사용자 할 일을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "주변 사용자 할 일 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TodoSuccessResponse.class,
                                    description = "지도(공유) 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "주변 사용자 할 일 조회 성공",
                                            value = """
                                            {
                                              "data": {
                                                "nickname": "mtripett1",
                                                "todos": [
                                                  {
                                                    "todoId": 589879,
                                                    "title": "Todo 4",
                                                    "category": "NEWS",
                                                    "isSaved": false
                                                  },
                                                  {
                                                    "todoId": 589880,
                                                    "title": "Todo 5",
                                                    "category": "NEWS",
                                                    "isSaved": false
                                                  },
                                                  {
                                                    "todoId": 589881,
                                                    "title": "Todo 6",
                                                    "category": "NEWS",
                                                    "isSaved": false
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
    SuccessResponse<SurroundingMemberInfo> getTodosForSurroundingMember(
            @RequestAttribute Long memberId,
            @Parameter(description = "주변 사용자 id", example = "9999") @RequestParam Long surroundingMemberId
    );

    @Operation(summary = "즐겨찾기 해제 API", description = "즐겨찾기에 추가한 할 일을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "즐겨찾기 해제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TodoSuccessResponse.class,
                                    description = "지도(공유) 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "즐겨찾기 해제 성공",
                                            value = "NO_CONTENT"
                                    )
                            }
                    )),
            @ApiResponse(responseCode = "404",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                        2. 주변 사용자의 할 일을 찾을 수 없는 경우(TODO_NOT_FOUND)
                        3. 주변 사용자의 할 일이 내 즐겨찾기 없는 경우(SAVE_TODO_NOT_FOUND_FROM_TARGET_PARENT)
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
                                            """),
                                    @ExampleObject(name = "주변 사용자 할 일 미존재 예시",
                                            value = """
                                            {
                                              "errorCode": "TODO_NOT_FOUND",
                                              "message": "해당 할 일이 존재하지 않습니다. todoId : 9999"
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "즐겨찾기에 할 일 미존재 예시",
                                            value = """
                                            {
                                              "errorCode": "SAVE_TODO_NOT_FOUND_FROM_TARGET_PARENT",
                                              "message": "내 즐겨찾기에 해당 할 일을 부모로 하는 할 일이 존재하지 않습니다. 해당 todoId: 9999"
                                            }
                                            """
                                    ),
                            }
                    )
            ),
    })
    void deleteTodo(
            @RequestAttribute Long memberId,
            @Parameter(description = "주변 사용자의 할 일 id", example = "9999") @RequestParam Long surroundingMemberTodoId
    );

}
