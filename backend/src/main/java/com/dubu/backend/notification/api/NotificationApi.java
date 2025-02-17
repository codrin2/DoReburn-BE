package com.dubu.backend.notification.api;

import com.dubu.backend.notification.dto.PushMessageDto;
import com.dubu.backend.notification.dto.PushSubscriptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;


public interface NotificationApi {

    @Operation(
            summary = "푸시 구독 등록",
            description = """
                    지정된 memberId에 대해 푸시 구독 정보를 등록합니다.
                    <p>구독 정보를 등록한 후, 서버에서 Web-Push를 전송할 수 있습니다.</p>
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "구독 정보 등록 성공 (응답 본문 없음)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원이 존재하지 않는 경우 (MEMBER_NOT_FOUND)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(
                                            name = "회원 미존재 에러 예시",
                                            value = """
                                                    {
                                                      "errorCode": "MEMBER_NOT_FOUND",
                                                      "message": "회원을 찾을 수 없습니다. memberId : 9999"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "푸시 서비스 문제로 인해 구독 정보를 등록할 수 없는 경우 (UNAVAILABLE_PUSH_SERVICE)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(
                                            name = "푸시 서비스 장애 에러 예시",
                                            value = """
                                                    {
                                                      "errorCode": "UNAVAILABLE_PUSH_SERVICE",
                                                      "message": "현재 푸시 서비스를 이용할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    void subscribe(
            Long memberId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "푸시 구독 요청 DTO",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PushSubscriptionDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "푸시 구독 요청 예시",
                                            value = """
                                                    {
                                                      "endpoint": "https://fcm.googleapis.com/fcm/send/someUniqueId",
                                                      "keys": {
                                                        "p256dh": "base64EncodedPublicKey",
                                                        "auth": "base64EncodedAuthKey"
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            PushSubscriptionDto subscription
    );

    @Operation(
            summary = "푸시 알림 전송",
            description = """
                    푸시 알림은 계획 생성 시 자동으로 전송됩니다. 이 API는 계획 생성없이 푸시 알림을 전송할 때(테스트) 사용합니다.
                    특정 회원에게 푸시 알림을 전송합니다.
                    이미 구독 정보가 등록되어 있어야 하며, 
                    요청 바디에서 memberId와 알림 내용(title, body)을 전달하면 
                    푸시 서버(WebPush)를 통해 알림이 발송됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "푸시 알림 전송 성공 (응답 본문 없음)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원이 존재하지 않는 경우 (MEMBER_NOT_FOUND)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(
                                            name = "회원 미존재 에러 예시",
                                            value = """
                                                    {
                                                      "errorCode": "MEMBER_NOT_FOUND",
                                                      "message": "회원을 찾을 수 없습니다. memberId : 9999"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "푸시 서비스 문제로 인해 알림 전송에 실패한 경우 (UNAVAILABLE_PUSH_SERVICE)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(
                                            name = "푸시 서비스 장애 에러 예시",
                                            value = """
                                                    {
                                                      "errorCode": "UNAVAILABLE_PUSH_SERVICE",
                                                      "message": "현재 푸시 서비스를 이용할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    void sendNotification(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "푸시 알림 전송 요청 DTO",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PushMessageDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "푸시 알림 전송 요청 예시",
                                            value = """
                                                    {
                                                      "memberId": 9999,
                                                      "title": "푸시 알림 제목",
                                                      "body": "푸시 알림 내용"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            PushMessageDto message
    );

    /**
     * 에러 응답 예시 스키마
     */
    @Schema(name = "ErrorResponseExample", description = "에러 응답 예시")
    class ErrorResponseExample {
        @Schema(example = "MEMBER_NOT_FOUND", description = "에러 코드")
        public String errorCode;

        @Schema(example = "회원을 찾을 수 없습니다. memberId : 9999", description = "에러 상세 메시지")
        public String message;
    }
}