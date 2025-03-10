package com.dubu.backend.notification.api;

import com.dubu.backend.notification.api.dto.FcmTokenDto;
import com.dubu.backend.notification.api.dto.PushSubscriptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
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
                    responseCode = "409",
                    description = "이미 해당 브라우저에서 푸시 구독이 완료된 경우 (DUPLICATE_SUBSCRIPTION)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseExample.class),
                            examples = {
                                    @ExampleObject(
                                            name = "중복 구독 에러 예시",
                                            value = """
                                                    {
                                                      "errorCode": "DUPLICATE_SUBSCRIPTION",
                                                      "message": "이미 알림을 구독한 브라우저입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    void subscribe(
            HttpServletRequest request,
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
            summary = "FCM 토큰 등록",
            description = """
                    FCM을 이용해 알림을 받기 위해서는 디바이스/브라우저에서 발급된 
                    토큰(deviceToken)을 서버에 등록해야 합니다. 
                    <p>이 API 호출 시 서버가 memberId와 FCM 토큰을 매핑하여 저장합니다.</p>
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "FCM 토큰 등록 성공 (응답 본문 없음)"
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
            )
    })
    void registerFcmToken(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "FCM 토큰 등록 요청 DTO",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FcmTokenDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "FCM 토큰 등록 예시",
                                            value = """
                                                    {
                                                      "deviceToken": "fcmTokenExample12345"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            FcmTokenDto fcmTokenDto
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