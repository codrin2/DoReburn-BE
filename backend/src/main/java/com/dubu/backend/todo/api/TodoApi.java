package com.dubu.backend.todo.api;

import com.dubu.backend.core.domain.PageResponse;
import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.todo.api.dto.TodoCursorDto;
import com.dubu.backend.todo.api.dto.request.*;
import com.dubu.backend.todo.api.dto.response.TodoCommandResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Todo API", description = "할 일 관리 API")
public interface TodoApi {
    @Operation(summary = "할 일 직접 생성", description = "새로운 할 일을 직접 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "할 일 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "직접 [오늘, 즐겨찾기, 경로별] 할 일 생성 성공",
                                            value = """
                                                    {
                                                        "data": {
                                                            "todoId": 84,
                                                            "category": "NEWS",
                                                            "difficulty": "EASY",
                                                            "title": "관심 산업 트렌드 찾아보기",
                                                            "memo": null
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "직접 내일 할 일 생성 성공 - 내일 스케줄이 생성된 경우",
                                            value = """
                                                    {
                                                        "isTomorrowScheduleCreated": false,
                                                        "data": {
                                                            "todoId": 84,
                                                            "category": "NEWS",
                                                            "difficulty": "EASY",
                                                            "title": "관심 산업 트렌드 찾아보기",
                                                            "memo": null
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "직접 내일 할 일 생성 성공 - 내일 스케줄이 생성되지 않은 경우",
                                            value = """
                                                    {
                                                        "isTomorrowScheduleCreated": true,
                                                        "data": [
                                                            {
                                                                "cursorTodoId": 84,
                                                                "category": "NEWS",
                                                                "difficulty": "EASY",
                                                                "title": "관심 산업 트렌드 찾아보기",
                                                                "memo": null
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            }
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원의 상태가 해당 API를 호출할 수 없는 경우 (INVALID_MEMBER_STATUS)
                        2. 할 일 개수를 초과한 경우 (TODO_LIMIT_EXCEEDED)
                        3. type 에 잘못된 값을 넣은 경우 (UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION)
                        4. type 이 PATH 일 때 pathId 가 주어지지 않은 경우 (PATH_ID_NOT_PROVIDED)
                    """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                            {
                                                "errorCode": "INVALID_MEMBER_STATUS",
                                                "message": "회원의 상태가 MOVE인 경우 해당 API를 이용할 수 없습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "할 일 개수 초과 예시",
                                            value = """
                                            {
                                                  "errorCode": "TODO_LIMIT_EXCEEDED",
                                                  "message": "내일의 할 일은 최대 5개까지 추가할 수 있습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "잘못된 타입(type) 예시",
                                            value = """
                                            {
                                                "errorCode": "UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION",
                                                "message": "TODAY 타입에 대해서 지원하지 않은 기능입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "주어지지 않은 pathId 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_ID_NOT_PROVIDED",
                                                "message": "경로 아이디가 누락되었습니다."
                                            }
                                            """
                                    )

                            }
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                        2. 카테고리를 찾을 수 없는 경우 (CATEGORY_NOT_FOUND)
                        3. 일정(Schedule)을 찾을 수 없는 경우 (SCHEDULE_NOT_FOUND)
                        4. 경로를 찾을 수 없는 경우 (PATH_NOT_FOUND)
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
                                    @ExampleObject(name = "카테고리 미존재 예시",
                                            value = """
                                            {
                                              "errorCode": "CATEGORY_NOT_FOUND",
                                              "message": "카테고리를 찾을 수 없습니다. categoryName : 'READING'"
                                            }
                                            """),
                                    @ExampleObject(name = "일정 미존재 예시",
                                            value = """
                                            {
                                              "errorCode": "SCHEDULE_NOT_FOUND",
                                              "message": "일정을 찾을 수 없습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "경로 미존재 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_NOT_FOUND",
                                                "message": "경로를 찾을 수 없습니다. pathId : 9999"
                                            }
                                            """
                                    )
                            }
                    )
            ),
    })
    @PostMapping("/{type}/manual")
    @ResponseStatus(HttpStatus.CREATED)
    TodoCommandResponse<?> postTodo(
            @RequestAttribute Long memberId,
            @PathVariable TodoRequestType type,
            @Nullable @RequestParam("pathId") Long pathId,
            @RequestBody TodoCreateRequest request
    );

    @Operation(summary = "다른 할 일로부터 할 일 생성 - 지도(공유)에서도 사용", description = "다른 할 일로부터 할 일을 추가합니다.지도(공유)에서 사용 시 type=FAVORITE 로 설정")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "할 일 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "회원 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "[오늘, 즐겨찾기, 경로별, 지도] 할 일 생성 성공",
                                            value = """
                                                    {
                                                        "data": {
                                                            "todoId": 84,
                                                            "category": "NEWS",
                                                            "difficulty": "EASY",
                                                            "title": "관심 산업 트렌드 찾아보기",
                                                            "memo": null
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "직접 내일 할 일 생성 성공 - 내일 스케줄이 생성된 경우",
                                            value = """
                                                    {
                                                        "isTomorrowScheduleCreated": false,
                                                        "data": {
                                                            "todoId": 84,
                                                            "category": "NEWS",
                                                            "difficulty": "EASY",
                                                            "title": "관심 산업 트렌드 찾아보기",
                                                            "memo": null
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "직접 내일 할 일 생성 성공 - 내일 스케줄이 생성되지 않은 경우",
                                            value = """
                                                    {
                                                        "isTomorrowScheduleCreated": true,
                                                        "data": [
                                                            {
                                                                "todoId": 84,
                                                                "category": "NEWS",
                                                                "difficulty": "EASY",
                                                                "title": "관심 산업 트렌드 찾아보기",
                                                                "memo": null
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            }
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원의 상태가 해당 API를 호출할 수 없는 경우 (INVALID_MEMBER_STATUS)
                        2. 할 일 개수를 초과한 경우 (TODO_LIMIT_EXCEEDED)
                        3. 이미 추가한 한 할 일의 경우 (ALREADY_ADDED_TODO)
                        4. type 에 잘못된 값을 넣은 경우 (UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION)
                        5. type 이 subPath 일 때 pathId 가 주어지지 않은 경우 (PATH_ID_NOT_PROVIDED)
                    """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                            {
                                                "errorCode": "INVALID_MEMBER_STATUS",
                                                "message": "회원의 상태가 MOVE인 경우 해당 API를 이용할 수 없습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "할 일 개수 초과 예시",
                                            value = """
                                            {
                                                  "errorCode": "TODO_LIMIT_EXCEEDED",
                                                  "message": "내일의 할 일은 최대 3개까지 추가할 수 있습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "이미 추가한 한 할 일 예시",
                                            value = """
                                            {
                                                "errorCode": "ALREADY_ADDED_TODO",
                                                "message": "이미 추가된 할 일 입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "잘못된 타입(type) 예시",
                                            value = """
                                            {
                                                "errorCode": "UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION",
                                                "message": "TODAY 타입에 대해서 지원하지 않은 기능입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "주어지지 않은 pathId 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_ID_NOT_PROVIDED",
                                                "message": "경로 아이디가 누락되었습니다."
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                        2. 할 일을 찾을 수 없는 경우 (TODO_NOT_FOUND)
                        3. 일정(Schedule)을 찾을 수 없는 경우 (SCHEDULE_NOT_FOUND)
                        4. 경로를 찾을 수 없는 경우 (PATH_NOT_FOUND)
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
                                    @ExampleObject(name = "할 일 미존재 예시",
                                            value = """
                                             {
                                                "errorCode": "TODO_NOT_FOUND",
                                                "message": "해당 할 일이 존재하지 않습니다. cursorTodoId : 9999"
                                             }
                                            """),
                                    @ExampleObject(name = "일정 미존재 예시",
                                            value = """
                                            {
                                              "errorCode": "SCHEDULE_NOT_FOUND",
                                              "message": "일정을 찾을 수 없습니다."
                                            }
                                            """),
                                    @ExampleObject(name = "경로 미존재 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_NOT_FOUND",
                                                "message": "경로를 찾을 수 없습니다. pathId : 9999"
                                            }
                                            """
                                    )
                            }
                    )
            ),

    })
    @PostMapping("/{type}/from-archived")
    @ResponseStatus(HttpStatus.CREATED)
    TodoCommandResponse<?> postTodoFromArchived(
            @RequestAttribute Long memberId,
            @PathVariable TodoRequestType type,
            @Nullable @RequestParam("pathId") Long pathId,
            @RequestBody TodoCreateFromArchiveRequest request
    );

    @Operation(summary = "할 일 수정", description = "할 일을 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "할 일 수정 성공 - 스케줄과 할 일 데이터 생성되지 않은 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "[오늘, 즐겨찾기, 경로별] 할 일 수정 성공",
                                            value = """
                                {
                                    "data": {
                                        "todoId": 84,
                                        "category": "NEWS",
                                        "difficulty": "EASY",
                                        "title": "관심 산업 트렌드 찾아보기",
                                        "memo": null
                                    }
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "내일 할 일 수정 성공 - 내일 스케줄 생성되지 않은 경우",
                                            value = """
                                {
                                    "isTomorrowScheduleCreated": false,
                                    "data": {
                                        "todoId": 84,
                                        "category": "NEWS",
                                        "difficulty": "EASY",
                                        "title": "관심 산업 트렌드 찾아보기",
                                        "memo": null
                                    }
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "201",
                    description = "할 일 수정 성공 - 스케줄과 할 일 데이터 생성된 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "내일 할 일 수정 성공 - 내일 스케줄 생성된 경우",
                                            value = """
                                {
                                    "isTomorrowScheduleCreated": true,
                                    "data": [
                                        {
                                            "todoId": 84,
                                            "category": "NEWS",
                                            "difficulty": "EASY",
                                            "title": "관심 산업 트렌드 찾아보기",
                                            "memo": null
                                        }
                                    ]
                                }
                                """
                                    )
                            }
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                    다음 경우에 발생할 수 있습니다:
                    1. 회원의 상태가 해당 API를 호출할 수 없는 경우 (INVALID_MEMBER_STATUS)
                    3. type 에 잘못된 값을 넣은 경우 (UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION)
                """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                        {
                                            "errorCode": "INVALID_MEMBER_STATUS",
                                            "message": "회원의 상태가 MOVE인 경우 해당 API를 이용할 수 없습니다."
                                        }
                                        """
                                    ),
                                    @ExampleObject(name = "일치하지 않은 할 일 타입과 요청 타입 예시",
                                            value = """
                                    {
                                        "errorCode": ""UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION",",
                                        "message": "TODAY 타입에 대해서 지원하지 않은 기능입니다."
                                    }
                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = """
                    다음 경우에 발생할 수 있습니다:
                    1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                    2. 할 일을 찾을 수 없는 경우 (TODO_NOT_FOUND)
                    3. 일정(Schedule)을 찾을 수 없는 경우 (SCHEDULE_NOT_FOUND)
                    4. 카테고리를 찾을 수 없는 경우 (CATEGORY_NOT_FOUND)
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
                                    @ExampleObject(name = "할 일 미존재 예시",
                                            value = """
                                        {
                                          "errorCode": "TODO_NOT_FOUND",
                                          "message": "해당 할 일이 존재하지 않습니다. cursorTodoId : 9999"
                                        }
                                        """
                                    ),
                                    @ExampleObject(name = "일정 미존재 예시",
                                            value = """
                              {
                                  "errorCode": "SCHEDULE_NOT_FOUND",
                                  "message": "일정을 찾을 수 없습니다."
                              }
                              """
                                    ),
                                    @ExampleObject(name = "카테고리 미존재 예시",
                                            value = """
                                {
                                  "errorCode": "CATEGORY_NOT_FOUND",
                                  "message": "카테고리를 찾을 수 없습니다. categoryName : 'READING'"
                                }
                        """),

                            }
                    )
            ),

    })
    @PatchMapping("/{todoId}")
    ResponseEntity<?> patchTodo(
            @RequestAttribute Long memberId,
            @PathVariable("todoId")Long todoId,
            @RequestParam("type") TodoRequestType type,
            @RequestBody TodoUpdateRequest request
    );


    @Operation(summary = "할 일 삭제", description = "할 일을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "내일 할 일 삭제 성공 - 내일 스케줄이 생성되지 않은 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "내일 할 일 삭제 성공 - 내일 스케줄 생성되지 않은 경우",
                                            value = """
                                            {
                                                "isTomorrowScheduleCreated": false,
                                                "data": {
                                                    "todoId": 84,
                                                    "category": "NEWS",
                                                    "difficulty": "EASY",
                                                    "title": "관심 산업 트렌드 찾아보기",
                                                    "memo": null
                                                }
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "201",
                    description = "할 일 삭제 성공 - 내일 스케줄 생성된 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "내일 할 일 삭제 성공 - 내일 스케줄 생성된 경우",
                                            value = """
                                            {
                                                "isTomorrowScheduleCreated": true,
                                                "data": [
                                                    {
                                                        "todoId": 84,
                                                        "category": "NEWS",
                                                        "difficulty": "EASY",
                                                        "title": "관심 산업 트렌드 찾아보기",
                                                        "memo": null
                                                    }
                                                ]
                                            }
                                            """
                                    )
                            }
                    )),
            @ApiResponse(responseCode = "204",
                    description = "[오늘, 즐겨찾기, 경로별] 할 일 삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "[오늘, 즐겨찾기, 경로별] 할 일 삭제 성공",
                                            value = "No Content"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                    다음 경우에 발생할 수 있습니다:
                    1. 회원의 상태가 해당 API를 호출할 수 없는 경우 (INVALID_MEMBER_STATUS)
                    2. type 에 잘못된 값을 넣은 경우 (UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION)
                """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                            {
                                                "errorCode": "INVALID_MEMBER_STATUS",
                                                "message": "회원의 상태가 MOVE인 경우 해당 API를 이용할 수 없습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "잘못된 타입(type) 예시",
                                            value = """
                                            {
                                                "errorCode": "UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION",
                                                "message": "TODAY 타입에 대해서 지원하지 않은 기능입니다."
                                            }
                                            """
                                    ),
                            }
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = """
                    다음 경우에 발생할 수 있습니다:
                    1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                    2. 할 일을 찾을 수 없는 경우 (TODO_NOT_FOUND)
                    3. 일정(Schedule)을 찾을 수 없는 경우 (SCHEDULE_NOT_FOUND)
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
                                            """
                                    ),
                                    @ExampleObject(name = "할 일 미존재 예시",
                                            value = """
                                            {
                                              "errorCode": "TODO_NOT_FOUND",
                                              "message": "해당 할 일이 존재하지 않습니다. cursorTodoId : 9999"
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "일정 미존재 예시",
                                            value = """
                                              {
                                                  "errorCode": "SCHEDULE_NOT_FOUND",
                                                  "message": "일정을 찾을 수 없습니다."
                                              }
                                              """
                                    ),
                            }
                    )
            ),

    })
    @DeleteMapping("/{todoId}")
    ResponseEntity<?> deleteTodo(
            @RequestAttribute Long memberId,
            @PathVariable("todoId") Long todoId,
            @RequestParam("type") TodoRequestType type
    );

    @Operation(summary = "할 일 조회", description = "할 일을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "할 일 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "할 일 조회 성공",
                                            value = """
                                            {
                                                "data": [
                                                    {
                                                        "todoId": 84,
                                                        "category": "NEWS",
                                                        "difficulty": "EASY",
                                                        "title": "관심 산업 트렌드 찾아보기",
                                                        "memo": null
                                                    }
                                                ]
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = """
                            1. 회원의 상태가 해당 API를 호출할 수 없는 경우 (INVALID_MEMBER_STATUS)
                            """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                            {
                                                "errorCode": "INVALID_MEMBER_STATUS",
                                                "message": "회원의 상태가 MOVE인 경우 해당 API를 이용할 수 없습니다."
                                            }
                                            """
                                    ),
                            }
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = """
                    다음 경우에 발생할 수 있습니다:
                    1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                    2. 회원의 카테고리 정보가 없는 경우 (MEMBER_CATEGORY_NOT_FOUND)
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
                                            """
                                    ),
                                    @ExampleObject(name = "회원 카테고리 정보 미존재 예시",
                                            value = """
                                              {
                                                  "errorCode": "MEMBER_CATEGORY_NOT_FOUND",
                                                  "message": "회원의 카테고리 정보를 찾을 수 없습니다. memberId : 9999"
                                              }
                                              """
                                    ),
                            }
                    )
            ),

    })
    @GetMapping("/{type}")
    @ResponseStatus(HttpStatus.OK)
    SuccessResponse<?> getTodos(
            @RequestAttribute Long memberId,
            @PathVariable("type") TodoRequestType type,
            @Nullable @RequestParam("pathId") Long subPathId
    );

    @Operation(summary = "추천 할 일 조회 - 더보기 X", description = "추천 할 일을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "추천 할 일 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "추천 할 일 조회 성공",
                                            value = """
                                            {
                                                "data": [
                                                    {
                                                        "todoId": 23,
                                                        "hasChild": false,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "간단한 언어 학습 퀴즈 풀기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 24,
                                                        "hasChild": false,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "기초 문법 공부하기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 25,
                                                        "hasChild": true,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "단어 외우기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 29,
                                                        "hasChild": true,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "해당 국가의 뉴스 기사 읽기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 32,
                                                        "hasChild": true,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "해당 국가의 노래 듣기",
                                                        "memo": null
                                                    }
                                                ]
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = """
                        다음 경우에 발생할 수 있습니다.
                        1. 회원의 상태가 해당 API를 호출할 없는 경우 (INVALID_MEMBER_STATUS)
                        2. type 에 잘못된 값을 넣은 경우 (UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION)
                        3. type 이 PATH 일 때 pathId 가 주어지지 않은 경우 (PATH_ID_NOT_PROVIDED)
                    """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                            {
                                                "errorCode": "INVALID_MEMBER_STATUS",
                                                "message": "회원의 상태가 MOVE인 경우 해당 API를 이용할 수 없습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "잘못된 타입(modifyType) 예시",
                                            value = """
                                            {
                                                "errorCode": "UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION",
                                                "message": "TODAY 타입에 대해서 지원하지 않은 기능입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "주어지지 않은 pathId 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_ID_NOT_PROVIDED",
                                                "message": "경로 아이디가 누락되었습니다."
                                            }
                                            """
                                    )
                            }
                    )

            ),
            @ApiResponse(responseCode = "404",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                        2. 스케줄을 찾을 수 없는 경우 (SCHEDULE_NOT_FOUND)
                        3. 경로를 찾을 수 없는 경우 (PATH_NOT_FOUND)
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
                                            """
                                    ),
                                    @ExampleObject(name = "일정 미존재 예시",
                                            value = """
                                              {
                                                  "errorCode": "SCHEDULE_NOT_FOUND",
                                                  "message": "일정을 찾을 수 없습니다."
                                              }
                                              """
                                    ),
                                    @ExampleObject(name = "경로 미존재 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_NOT_FOUND",
                                                "message": "경로를 찾을 수 없습니다. pathId : 9999"
                                            }
                                            """
                                    ),
                            }
                    )
            ),
    })
    @GetMapping("/recommend/personalized")
    @ResponseStatus(HttpStatus.OK)
    SuccessResponse<?> getRecommendTodos(
            @RequestAttribute Long memberId,
            @RequestParam("modifyType") String editType,
            @Nullable @RequestParam("pathId") Long subPathId
    );

    @Operation(summary = "즐겨찾기 할 일 조회", description = "즐겨찾기 할 일을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "즐겨찾기 할 일 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "즐겨찾기 할 일 조회 성공",
                                            value = """
                                            {
                                                "hasNext": false,
                                                "nextCursor": 81,
                                                "data": [
                                                    {
                                                        "todoId": 23,
                                                        "hasChild": false,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "간단한 언어 학습 퀴즈 풀기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 24,
                                                        "hasChild": false,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "기초 문법 공부하기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 25,
                                                        "hasChild": true,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "단어 외우기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 29,
                                                        "hasChild": true,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "해당 국가의 뉴스 기사 읽기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 32,
                                                        "hasChild": true,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "해당 국가의 노래 듣기",
                                                        "memo": null
                                                    }
                                                ]
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = """
                        다음 경우에 발생할 수 있습니다.
                        1. 회원의 상태가 해당 API를 호출할 없는 경우 (INVALID_MEMBER_STATUS)
                        2. type 에 잘못된 값을 넣은 경우 (UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION)
                        3. type 이 subPath 일 때 pathId 가 주어지지 않은 경우 (PATH_ID_NOT_PROVIDED)
                    """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                            {
                                                "errorCode": "INVALID_MEMBER_STATUS",
                                                "message": "회원의 상태가 MOVE인 경우 해당 API를 이용할 수 없습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "잘못된 타입 예시",
                                            value = """
                                            {
                                                "errorCode": "UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION",
                                                "message": "TODAY 타입에 대해서 지원하지 않은 기능입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "주어지지 않은 pathId 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_ID_NOT_PROVIDED",
                                                "message": "경로 아이디가 누락되었습니다."
                                            }
                                            """
                                    )
                            }
                    )

            ),
            @ApiResponse(responseCode = "404",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                        2. 스케줄을 찾을 수 없는 경우 (SCHEDULE_NOT_FOUND)
                        3. 경로를 찾을 수 없는 경우 (PATH_NOT_FOUND)
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
                                            """
                                    ),
                                    @ExampleObject(name = "일정 미존재 예시",
                                            value = """
                                              {
                                                  "errorCode": "SCHEDULE_NOT_FOUND",
                                                  "message": "일정을 찾을 수 없습니다."
                                              }
                                              """
                                    ),
                                    @ExampleObject(name = "경로 미존재 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_NOT_FOUND",
                                                "message": "경로를 찾을 수 없습니다. pathId : 9999"
                                            }
                                            """
                                    ),
                            }
                    )
            ),
    })
    @GetMapping("/favorite")
    @ResponseStatus(HttpStatus.OK)
    PageResponse<?, ?> getFavoriteTodos(
            @RequestAttribute Long memberId,
            @RequestParam("modifyType") String editType,
            @Nullable @RequestParam("pathId") Long subPathId,
            @RequestParam(name = "cursor", required = false) Long todoId,
            @RequestParam Integer size
    );

    @Operation(summary = "모든 추천 할 일 조회 - 더보기 O", description = "모든 추천 할 일을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "모든 추천 할 일 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "추천 할 일 조회 성공",
                                            value = """
                                            {
                                                "hasNext": true,
                                                "nextCursor": {
                                                    "cursorCategoryId": 3,
                                                    "cursorDifficulty": "EASY",
                                                    "cursorTodoId": 32
                                                },
                                                "data": [
                                                    {
                                                        "todoId": 23,
                                                        "hasChild": false,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "간단한 언어 학습 퀴즈 풀기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 24,
                                                        "hasChild": false,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "기초 문법 공부하기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 25,
                                                        "hasChild": true,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "단어 외우기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 29,
                                                        "hasChild": true,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "해당 국가의 뉴스 기사 읽기",
                                                        "memo": null
                                                    },
                                                    {
                                                        "todoId": 32,
                                                        "hasChild": true,
                                                        "category": "LANGUAGE",
                                                        "difficulty": "EASY",
                                                        "title": "해당 국가의 노래 듣기",
                                                        "memo": null
                                                    }
                                                ]
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = """
                        다음 경우에 발생할 수 있습니다.
                        1. 회원의 상태가 해당 API를 호출할 없는 경우 (INVALID_MEMBER_STATUS)
                        2. type 에 잘못된 값을 넣은 경우 (UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION)
                        3. type 이 subPath 일 때 pathId 가 주어지지 않은 경우 (PATH_ID_NOT_PROVIDED)
                    """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                            {
                                                "errorCode": "INVALID_MEMBER_STATUS",
                                                "message": "회원의 상태가 MOVE인 경우 해당 API를 이용할 수 없습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "잘못된 타입(modifyType) 예시",
                                            value = """
                                            {
                                                "errorCode": "UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION",
                                                "message": "TODAY 타입에 대해서 지원하지 않은 기능입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "주어지지 않은 pathId 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_ID_NOT_PROVIDED",
                                                "message": "경로 아이디가 누락되었습니다."
                                            }
                                            """
                                    )
                            }
                    )

            ),
            @ApiResponse(responseCode = "404",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                        2. 스케줄을 찾을 수 없는 경우 (SCHEDULE_NOT_FOUND)
                        3. 경로를 찾을 수 없는 경우 (PATH_NOT_FOUND)
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
                                            """
                                    ),
                                    @ExampleObject(name = "일정 미존재 예시",
                                            value = """
                                              {
                                                  "errorCode": "SCHEDULE_NOT_FOUND",
                                                  "message": "일정을 찾을 수 없습니다."
                                              }
                                              """
                                    ),
                                    @ExampleObject(name = "경로 미존재 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_NOT_FOUND",
                                                "message": "경로를 찾을 수 없습니다. pathId : 9999"
                                            }
                                            """
                                    ),
                            }
                    )
            ),
    })
    @GetMapping("/recommend/all")
    @ResponseStatus(HttpStatus.OK)
    PageResponse<?, ?> getRecommendTodosPaged(
            @RequestAttribute Long memberId,
            @RequestParam("modifyType") String editType,
            @Nullable @RequestParam("pathId") Long subPathId,
            @Nullable @ModelAttribute TodoCursorDto cursor,
            @RequestParam Integer size,
            @ModelAttribute RecommendTodoFilterRequest request
    );

    @Operation(summary = "할 일의 경로 수정", description = "드래그-드랍을 하여 할 일의 경로를 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "할 일의 경로 수정 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "할 일의 경로 수정 성공",
                                            value = "NO CONTENT"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = """
                        다음 경우에 발생할 수 있습니다.
                        1. 회원의 상태가 해당 API를 호출할 없는 경우 (INVALID_MEMBER_STATUS)
                        2. type 에 잘못된 값을 넣은 경우 (UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION)
                    """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                            {
                                                "errorCode": "INVALID_MEMBER_STATUS",
                                                "message": "회원의 상태가 STOP인 경우 해당 API를 이용할 수 없습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(name = "일치하지 않은 할 일 타입과 요청 타입 예시",
                                            value = """
                                            {
                                                "errorCode": "UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION",
                                                "message": "TODAY 타입에 대해서 지원하지 않은 기능입니다."
                                            }
                                            """
                                    )
                            }
                    )

            ),
            @ApiResponse(responseCode = "404",
                    description = """
                        다음 경우에 발생할 수 있습니다:
                        1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                        2. 경로를 찾을 수 없는 경우 (PATH_NOT_FOUND)
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
                                            """
                                    ),
                                    @ExampleObject(name = "경로 미존재 예시",
                                            value = """
                                            {
                                                "errorCode": "PATH_NOT_FOUND",
                                                "message": "경로를 찾을 수 없습니다. pathId : 9999"
                                            }
                                            """
                                    )
                            }
                    )
            ),
    })
    @PatchMapping("/path")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void patchTodoPath(
            @RequestAttribute("memberId") Long memberId,
            @RequestParam Long todoId,
            @ModelAttribute TodoPathUpdateRequest request
    );

    @Operation(summary = "경로 별 할 일 완료 체크", description = "경로 별 할 일에 대하여 완료 체크한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "경로별 할 일 완료 체크 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
//                                    implementation = TodoSuccessResponse.class,
                                    description = "할 일 성공 응답"
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "경로별 할 일 완료 체크 성공",
                                            value = "No Content"
                                    ),
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                    다음 경우에 발생할 수 있습니다:
                    1. 회원의 상태가 해당 API를 호출할 수 없는 경우 (INVALID_MEMBER_STATUS)
                    2. type 에 잘못된 값을 넣은 경우 (UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION)
                    """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TodoApi.ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원의 상태 예시",
                                            value = """
                                                    {
                                                        "errorCode": "INVALID_MEMBER_STATUS",
                                                        "message": "회원의 상태가 MOVE인 경우 해당 API를 이용할 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(name = "일치하지 않은 할 일 타입과 요청 타입 예시",
                                            value = """
                                                    {
                                                        "errorCode": "UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION",
                                                        "message": "TODAY 타입에 대해서 지원하지 않은 기능입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = """
                    다음 경우에 발생할 수 있습니다:
                    1. 회원을 찾을 수 없는 경우 (MEMBER_NOT_FOUND)
                    2. 할 일을 찾을 수 없는 경우 (TODO_NOT_FOUND)
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
                                    @ExampleObject(name = "할 일 미존재 예시",
                                            value = """
                                        {
                                          "errorCode": "TODO_NOT_FOUND",
                                          "message": "해당 할 일이 존재하지 않습니다. cursorTodoId : 9999"
                                        }
                                        """
                                    )
                            }
                    )
            ),

    })
    @PatchMapping("/check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void patchTodoCompletionStatus(
            @RequestAttribute Long memberId,
            @RequestParam("todoId") Long todoId,
            @RequestBody TodoCompletionToggleRequest request
    );

    @Schema(name = "ErrorResponseExample", description = "에러 응답 예시")
    class ErrorResponseExample {
        @Schema(example = "MEMBER_NOT_FOUND")
        public String errorCode;
        @Schema(example = "회원을 찾을 수 없습니다. memberId : 9999")
        public String message;
    }
}
