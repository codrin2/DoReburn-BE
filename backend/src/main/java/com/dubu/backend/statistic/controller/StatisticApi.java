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
                                                          "dayAvailableTimes": [
                                                            {
                                                              "date": "2025-01-13",
                                                              "availableTime": 69
                                                            },
                                                            {
                                                              "date": "2025-01-14",
                                                              "availableTime": 132
                                                            },
                                                            {
                                                              "date": "2025-01-15",
                                                              "availableTime": 40
                                                            },
                                                            {
                                                              "date": "2025-01-16",
                                                              "availableTime": 0
                                                            },
                                                            {
                                                              "date": "2025-01-17",
                                                              "availableTime": 87
                                                            },
                                                            {
                                                              "date": "2025-01-18",
                                                              "availableTime": 40
                                                            },
                                                            {
                                                              "date": "2025-01-19",
                                                              "availableTime": 200
                                                            }
                                                          ],
                                                          "totalTodoCount": 37,
                                                          "lastWeekDiff": 173,
                                                          "totalAvailableTime": 568,
                                                          "moodCounts": [
                                                            {
                                                              "mood": "DISSATISFIED",
                                                              "count": 3
                                                            },
                                                            {
                                                              "mood": "MODERATE",
                                                              "count": 5
                                                            },
                                                            {
                                                              "mood": "SATISFIED",
                                                              "count": 6
                                                            }
                                                          ],
                                                          "categoryTodoCounts": [
                                                            {
                                                              "category": "NEWS",
                                                              "usageTime": 53,
                                                              "count": 9
                                                            },
                                                            {
                                                              "category": "ENGLISH",
                                                              "usageTime": 46,
                                                              "count": 6
                                                            },
                                                            {
                                                              "category": "OTHERS",
                                                              "usageTime": 35,
                                                              "count": 6
                                                            },
                                                            {
                                                              "category": "HOBBY",
                                                              "usageTime": 29,
                                                              "count": 5
                                                            },
                                                            {
                                                              "category": "LANGUAGE",
                                                              "usageTime": 27,
                                                              "count": 6
                                                            },
                                                            {
                                                              "category": "READING",
                                                              "usageTime": 23,
                                                              "count": 5
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