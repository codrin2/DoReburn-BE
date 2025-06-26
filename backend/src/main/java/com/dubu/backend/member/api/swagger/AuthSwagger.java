package com.dubu.backend.member.api.swagger;

import com.dubu.backend.core.domain.ErrorResponse;
import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.member.api.response.AccessToken;
import com.dubu.backend.member.api.response.Token;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

public interface AuthSwagger {
    @Operation(
            summary = "카카오 로그인 콜백 처리",
            description = """
                    카카오 로그인 후, 카카오에서 전달받은 인가 코드(code)를 요청 바디로 전송하면 
                    해당 코드를 사용하여 액세스 토큰을 발급받고 카카오 회원 정보를 조회한 뒤, 
                    서버 측에서 자체 토큰을 발급한다. AccessToken은 Body로 RefershToken은 Cookie로 전달한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "카카오 로그인 성공 후 자체 토큰 발급 성공",
                    content = @Content(
                            schema = @Schema(implementation = TokenResponseExample.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                              "data": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                              }
                                            }
                                            """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 code 요청 등으로 인한 로그인 실패 가능",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseExample.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                              "errorCode": "BAD_REQUEST",
                                              "message": "로그인 요청이 잘못되었습니다."
                                            }
                                            """)
                            }
                    )
            )
    })
    SuccessResponse<AccessToken> kakaoLogin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "카카오에서 인가 코드를 전달받을 때 사용되는 필드(code)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = KakaoLoginRequestExample.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                              "code": "sample_kakao_authorization_code"
                                            }
                                            """)
                            }
                    )
            )
            Map<String, String> request,
            HttpServletResponse response
    );

    @Operation(
            summary = "토큰 재발급",
            description = """
                    만료된 액세스 토큰을 재발급한다. 
                    헤더에 Authorization: Bearer {기존 토큰} 을 담아 요청해야 하며, 
                    서버 내부적으로 Refresh Token 유효성 검사를 수행한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "액세스 토큰 재발급 성공",
                    content = @Content(
                            schema = @Schema(implementation = TokenResponseExample.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                              "data": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                              }
                                            }
                                            """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            쿠키에 존재하지 않거나/블랙리스트 토큰 등으로 인해 재발급이 불가능한 경우
                            (TOKEN_INVALID, TOKEN_BLACKLISTED, MISSING_TOKEN_IN_COOKIE, REFRESH_TOKEN_EXPIRED)
                            """,
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseExample.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "TOKEN_INVALID", value = """
                                            {
                                              "errorCode": "TOKEN_INVALID",
                                              "message": "유효하지 않은 토큰입니다. 다시 로그인해 주세요."
                                            }
                                            """),
                                    @ExampleObject(name = "TOKEN_BLACKLISTED", value = """
                                            {
                                              "errorCode": "TOKEN_BLACKLISTED",
                                              "message": "해당 토큰은 사용이 금지되었습니다. 다시 로그인해 주세요."
                                            }
                                            """),
                                    @ExampleObject(name = "MISSING_TOKEN_IN_COOKIE", value = """
                                            {
                                              "errorCode": "MISSING_TOKEN_IN_COOKIE",
                                              "message": "쿠키에 토큰이 존재하지 않습니다. 다시 로그인해 주세요."
                                            }
                                            """),
                                    @ExampleObject(name = "REFRESH_TOKEN_EXPIRED", value = """
                                            {
                                              "errorCode": "REFRESH_TOKEN_EXPIRED",
                                              "message": "리프레쉬 토큰이 만료되었습니다. 다시 로그인해 주세요."
                                            }
                                            """)
                            }
                    )
            )
    })
    SuccessResponse<AccessToken> reissue(
            String refreshToken,
            HttpServletResponse response
    );

    /*──────────────────────────────────────────────────────
     * 로그아웃
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "로그아웃",
            description = """
            쿠키에 위치한 Refresh Token을 블랙리스트 처리해 재사용을 차단합니다.<br>
            클라이언트 측에서 기존에 관리하던 AccessToken을 제거해야합니다
            """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "로그아웃 완료")
            }
    )
    @PostMapping("/logout")
    void logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    );

    /*──────────────────────────────────────────────────────
     * 회원 탈퇴
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "회원 탈퇴",
            description = """
            존재하는 회원인지 여부를 확인한 후 회원의 계정을 삭제합니다.
            """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "탈퇴 완료"),
                    @ApiResponse(responseCode = "404",
                            description = "NOT_FOUND_MEMBER",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "NOT_FOUND_MEMBER",
                          "message": "회원을 찾을 수 없습니다. MemberId : 42"
                        }"""
                                    )))
            }
    )
    @DeleteMapping("/account")
    void deleteAccount(
            @RequestAttribute("memberId") Long memberId
    );

    /*──────────────────────────────────────────────────────
     * 테스트용 토큰 발급
     *──────────────────────────────────────────────────────*/
    @Operation(
            summary = "테스트용 토큰 발급",
            description = "회원 1번(고정)에 대한 임시 액세스 토큰을 발급한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "테스트용 토큰 발급 성공",
                    content = @Content(
                            schema = @Schema(implementation = TokenResponseExample.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                              "data": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                              }
                                            }
                                            """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            AccessToken 만료 시 혹은 AccessToken 변조 시 에러가 발생한다.
                            (TOKEN_EXPIRED, TOKEN_INVALID)
                            """,
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseExample.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = """
                                            {
                                              "errorCode": "TOKEN_EXPIRED",
                                              "message": "토큰이 만료되었습니다. 새로운 토큰을 재발급 받으세요."
                                            }
                                            """),
                                    @ExampleObject(name = "TOKEN_INVALID", value = """
                                            {
                                              "errorCode": "TOKEN_INVALID",
                                              "message": "유효하지 않은 토큰입니다. 다시 로그인해 주세요."
                                            }
                                            """)
                            }
                    )
            )
    })
    SuccessResponse<AccessToken> testToken();

    class ErrorResponseExample {
        public String errorCode;
        public String message;
    }

    class TokenResponseExample {
        public Token data;
    }

    class KakaoLoginRequestExample {
        public String code;
    }
}
