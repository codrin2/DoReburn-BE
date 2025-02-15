package com.dubu.backend.statistic.controller;

import com.dubu.backend.global.domain.SuccessResponse;
import com.dubu.backend.statistic.dto.response.DayStatisticInfo;
import com.dubu.backend.statistic.dto.response.WeekStatisticInfo;
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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Statistic API", description = "통계 API")
public interface StatisticApi {
    @Operation(summary = "일일 통계 데이터 조회", description = "일일 통게 데이터를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "일일 통계 데이터 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TodoSuccessResponse.class,
                                    description = "통계 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "일일 통계 데이터가 있을 경우",
                                            value = """
                                            {
                                                "data": {
                                                    "totalMoveTime": 224,
                                                    "totalUsageTime": 100,
                                                    "feedbacks": [
                                                        {
                                                            "mood": "DISSATISFIED",
                                                            "memo": "Feedback P"
                                                        },
                                                        {
                                                            "mood": "DISSATISFIED",
                                                            "memo": "Feedback F"
                                                        },
                                                        {
                                                            "mood": "MODERATE",
                                                            "memo": "Feedback G"
                                                        }
                                                    ],
                                                    "categoryTodoCounts": [
                                                        {
                                                            "category": "OTHERS",
                                                            "count": 10
                                                        },
                                                        {
                                                            "category": "ENGLISH",
                                                            "count": 10
                                                        },
                                                        {
                                                            "category": "LANGUAGE",
                                                            "count": 4
                                                        },
                                                        {
                                                            "category": "NEWS",
                                                            "count": 4
                                                        },
                                                        {
                                                            "category": "HOBBY",
                                                            "count": 4
                                                        },
                                                        {
                                                            "category": "READING",
                                                            "count": 4
                                                        }
                                                    ]
                                                }
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "일일 통계 데이터가 없는 경우",
                                            value = """
                                            {
                                                "data": null
                                            }
                                            """
                                    ),
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
    SuccessResponse<DayStatisticInfo> getDayStatistic(
            @RequestAttribute Long memberId,
            @RequestParam LocalDate date
    );
    @Operation(summary = "주간 통계 데이터 조회", description = "주간 통게 데이터를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "주간 통계 데이터 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TodoSuccessResponse.class,
                                    description = "통계 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "주간 통계 데이터가 있을 경우",
                                            value = """
                                            {
                                                 "data": {
                                                     "dayUsageTimes": [
                                                         {
                                                             "date": "2025-01-27",
                                                             "usageTime": 227
                                                         },
                                                         {
                                                             "date": "2025-01-28",
                                                             "usageTime": 140
                                                         },
                                                         {
                                                             "date": "2025-01-29",
                                                             "usageTime": 70
                                                         },
                                                         {
                                                             "date": "2025-01-30",
                                                             "usageTime": 224
                                                         },
                                                         {
                                                             "date": "2025-01-31",
                                                             "usageTime": 160
                                                         },
                                                         {
                                                             "date": "2025-02-01",
                                                             "usageTime": 0
                                                         },
                                                         {
                                                             "date": "2025-02-02",
                                                             "usageTime": 0
                                                         }
                                                     ],
                                                     "totalTodoCount": 180,
                                                     "lastWeekDiff": -697,
                                                     "totalMoveTime": 821,
                                                     "totalUsageTime": 558,
                                                     "categoryTodoCounts": [
                                                         {
                                                             "category": "ENGLISH",
                                                             "usageTime": 128,
                                                             "count": 36
                                                         },
                                                         {
                                                             "category": "LANGUAGE",
                                                             "usageTime": 114,
                                                             "count": 38
                                                         },
                                                         {
                                                             "category": "OTHERS",
                                                             "usageTime": 84,
                                                             "count": 30
                                                         },
                                                         {
                                                             "category": "NEWS",
                                                             "usageTime": 80,
                                                             "count": 22
                                                         },
                                                         {
                                                             "category": "HOBBY",
                                                             "usageTime": 78,
                                                             "count": 30
                                                         },
                                                         {
                                                             "category": "READING",
                                                             "usageTime": 74,
                                                             "count": 24
                                                         }
                                                     ]
                                                 }
                                             }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "주간 통계 데이터가 없는 경우",
                                            value = """
                                            {
                                                "data": null
                                            }
                                            """
                                    ),
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
    SuccessResponse<WeekStatisticInfo> getWeekStatistic(
            @RequestAttribute Long memberId,
            @RequestParam LocalDate startDate
    );
}
