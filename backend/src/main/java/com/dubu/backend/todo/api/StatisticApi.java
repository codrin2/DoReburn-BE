package com.dubu.backend.todo.api;

import com.dubu.backend.global.domain.SuccessResponse;
import com.dubu.backend.todo.dto.response.DayStatisticInfo;
import com.dubu.backend.todo.dto.response.WeekStatisticInfo;
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
                                                        "memberCreateDate": "2023-02-05",
                                                        "totalUsageTime": 264,
                                                        "totalTodoCount": 10,
                                                        "feedbacks": [
                                                          {
                                                            "mood": "MODERATE",
                                                            "memo": "feedback_974765"
                                                          },
                                                          {
                                                            "mood": "SATISFIED",
                                                            "memo": "feedback_978139"
                                                          },
                                                          {
                                                            "mood": "MODERATE",
                                                            "memo": "feedback_983482"
                                                          }
                                                        ],
                                                        "categoryTodoCounts": [
                                                          {
                                                            "category": "HOBBY",
                                                            "count": 4
                                                          },
                                                          {
                                                            "category": "NEWS",
                                                            "count": 3
                                                          },
                                                          {
                                                            "category": "ENGLISH",
                                                            "count": 2
                                                          },
                                                          {
                                                            "category": "LANGUAGE",
                                                            "count": 1
                                                          }
                                                        ]
                                                      }
                                                    }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "계획은 있으나 한 일이 없는 경우",
                                            value = """
                                            {
                                              "data": {
                                                "memberCreateDate": "2023-02-05",
                                                "totalUsageTime": 70,
                                                "totalTodoCount": 0,
                                                "feedbacks": [
                                                  {
                                                    "mood": "SATISFIED",
                                                    "memo": "feedback_990214\\r"
                                                  }
                                                ],
                                                "categoryTodoCounts": []
                                              }
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "일일 통계 데이터가 없는 경우",
                                            value = """
                                            {
                                                "data": {
                                                    "memberCreateDate" :"2025-02-17"
                                                }
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
                                                         "memberCreateDate": "2023-02-05",
                                                         "dayAvailableTimes": [
                                                           {
                                                             "date": "2025-01-27",
                                                             "availableTime": 176
                                                           },
                                                           {
                                                             "date": "2025-01-28",
                                                             "availableTime": 46
                                                           },
                                                           {
                                                             "date": "2025-01-29",
                                                             "availableTime": 41
                                                           },
                                                           {
                                                             "date": "2025-01-30",
                                                             "availableTime": 98
                                                           },
                                                           {
                                                             "date": "2025-01-31",
                                                             "availableTime": 34
                                                           },
                                                           {
                                                             "date": "2025-02-01",
                                                             "availableTime": 0
                                                           },
                                                           {
                                                             "date": "2025-02-02",
                                                             "availableTime": 0
                                                           }
                                                         ],
                                                         "totalTodoCount": 32,
                                                         "lastWeekDiff": -128,
                                                         "totalAvailableTime": 395,
                                                         "moodCounts": [
                                                           {
                                                             "mood": "MODERATE",
                                                             "count": 5
                                                           },
                                                           {
                                                             "mood": "SATISFIED",
                                                             "count": 5
                                                           }
                                                         ],
                                                         "categoryTodoCounts": [
                                                           {
                                                             "category": "NEWS",
                                                             "usageTime": 37,
                                                             "count": 8
                                                           },
                                                           {
                                                             "category": "OTHERS",
                                                             "usageTime": 35,
                                                             "count": 4
                                                           },
                                                           {
                                                             "category": "ENGLISH",
                                                             "usageTime": 29,
                                                             "count": 6
                                                           },
                                                           {
                                                             "category": "LANGUAGE",
                                                             "usageTime": 28,
                                                             "count": 4
                                                           },
                                                           {
                                                             "category": "HOBBY",
                                                             "usageTime": 24,
                                                             "count": 6
                                                           },
                                                           {
                                                             "category": "READING",
                                                             "usageTime": 15,
                                                             "count": 4
                                                           }
                                                         ]
                                                       }
                                                     }
                                           """
                                    ),
                                    @ExampleObject(
                                            name = "계획은 있으나 한 일이 없는 경우",
                                            value = """
                                                    {
                                                      "data": {
                                                        "memberCreateDate": "2023-02-05",
                                                        "dayAvailableTimes": [
                                                          {
                                                            "date": "2025-01-31",
                                                            "availableTime": 34
                                                          },
                                                          {
                                                            "date": "2025-02-01",
                                                            "availableTime": 0
                                                          },
                                                          {
                                                            "date": "2025-02-02",
                                                            "availableTime": 0
                                                          },
                                                          {
                                                            "date": "2025-02-03",
                                                            "availableTime": 0
                                                          },
                                                          {
                                                            "date": "2025-02-04",
                                                            "availableTime": 0
                                                          },
                                                          {
                                                            "date": "2025-02-05",
                                                            "availableTime": 0
                                                          },
                                                          {
                                                            "date": "2025-02-06",
                                                            "availableTime": 0
                                                          }
                                                        ],
                                                        "totalTodoCount": 0,
                                                        "lastWeekDiff": -470,
                                                        "totalAvailableTime": 34,
                                                        "moodCounts": [
                                                          {
                                                            "mood": "SATISFIED",
                                                            "count": 1
                                                          }
                                                        ],
                                                        "categoryTodoCounts": []
                                                    }
                                                }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "주간 통계 데이터가 없는 경우",
                                            value = """
                                            {
                                                "memberCreateDate": "2023-02-05",
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