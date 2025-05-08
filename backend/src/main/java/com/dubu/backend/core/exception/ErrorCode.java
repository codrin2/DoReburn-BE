package com.dubu.backend.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_JSON(BAD_REQUEST,"잘못된 JSON 형식입니다. 요청 데이터를 확인하세요."),
    FIELD_ERROR(BAD_REQUEST,"입력이 잘못되었습니다."),
    URL_PARAMETER_ERROR(BAD_REQUEST,"입력이 잘못되었습니다."),
    METHOD_ARGUMENT_TYPE_MISMATCH(BAD_REQUEST,"입력한 값의 타입이 잘못되었습니다."),
    ALREADY_DISCONNECTED(BAD_REQUEST,"이미 클라이언트에서 요청이 종료되었습니다."),
    NOT_FOUND_COOKIE(BAD_REQUEST,"일치하는 쿠키가 없습니다."),
    NO_RESOURCE_FOUND(NOT_FOUND,"요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_SUPPORTED(METHOD_NOT_ALLOWED,"허용되지 않은 메서드입니다."),
    MEDIA_TYPE_NOT_SUPPORTED(UNSUPPORTED_MEDIA_TYPE,"허용되지 않은 미디어 타입입니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR,"서버 오류가 발생했습니다. 관리자에게 문의하세요."),
    REDIS_UNAVAILABLE(INTERNAL_SERVER_ERROR,"Redis 서버에 연결할 수 없습니다."),

    // Token
    INVALID_TOKEN_HEADER(UNAUTHORIZED, "토큰 헤더 형식이 잘못되었습니다."),
    TOKEN_INVALID(UNAUTHORIZED, "유효하지 않은 토큰입니다. 다시 로그인해 주세요."),
    TOKEN_MISSING(UNAUTHORIZED, "토큰이 요청 헤더에 없습니다. 새로운 토큰을 재발급 받으세요"),
    TOKEN_BLACKLISTED(UNAUTHORIZED, "해당 토큰은 사용이 금지되었습니다. 다시 로그인해 주세요."),
    TOKEN_EXPIRED(UNAUTHORIZED, "토큰이 만료되었습니다. 새로운 토큰을 재발급 받으세요."),
    MISSING_TOKEN_IN_COOKIE(UNAUTHORIZED, "쿠키에 토큰이 존재하지 않습니다. 다시 로그인해 주세요."),
    REFRESH_TOKEN_EXPIRED(UNAUTHORIZED, "리프레쉬 토큰이 만료되었습니다. 다시 로그인해 주세요."),

    // OAuth
    UNSUPPORTED_SOCIAL_LOGIN(BAD_REQUEST, "지원하지 않는 소셜 로그인 타입입니다."),

    // Member
    INVALID_MEMBER_STATUS(BAD_REQUEST, "회원의 상태가 %s인 경우 해당 API를 이용할 수 없습니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "회원을 찾을 수 없습니다. memberId : %d"),

    // Address
    MEMBER_SAVED_ADDRESS_NOT_FOUND(NOT_FOUND, "회원이 저장한 주소를 찾을 수 없습니다. memberId : %d"),

    // Subscription
    DUPLICATE_SUBSCRIPTION(CONFLICT, "이미 알림을 구독한 브라우저입니다."),

    // Category
    CATEGORY_NOT_FOUND(NOT_FOUND, "카테고리를 찾을 수 없습니다. categoryName : %s"),

    // Plan
    NOT_FOUND_PLAN(NOT_FOUND, "계획을 찾을 수 없습니다. planId : %d"),
    UNAUTHORIZED_PLAN_DELETION(UNAUTHORIZED, "회원이 해당 계획에 접근 권한이 없어 삭제할 수 없습니다. memberId : %d, planId : %d"),

    // Feedback
    INVALID_MOOD(BAD_REQUEST, "유효하지 않은 기분 형식입니다. mood : %s"),

    // Path
    PATH_NOT_FOUND(NOT_FOUND, "경로를 찾을 수 없습니다. pathId : %d"),
    PATH_ID_NOT_PROVIDED(BAD_REQUEST, "경로 아이디가 누락되었습니다."),
    INVALID_TRAFFIC_TYPE(BAD_REQUEST, "지원하지 않는 대중교통 형식입니다. trafficType : %s"),

    // Member_Category
    MEMBER_CATEGORY_NOT_FOUND(NOT_FOUND, "회원의 카테고리 정보를 찾을 수 없습니다. memberId : %d"),

    // Todo
    TODO_NOT_FOUND(NOT_FOUND, "해당 할 일이 존재하지 않습니다. todoId : %d"),
    UNSUPPORTED_OPERATION_FOR_TODO_REQUEST_TYPE_EXCEPTION(BAD_REQUEST, "%s 타입에 대해서 지원하지 않은 기능입니다."),

    INVALID_TODO_DIFFICULTY(BAD_REQUEST, "유효하지 않을 할 일 난이도입니다. cursorDifficulty = %s"),
    INVALID_TODO_TYPE(BAD_REQUEST, "유효하지 않은 할 일 유형입니다. type = %s"),
    INVALID_MODIFY_PAGE_TYPE(BAD_REQUEST, "유효하지 않은 수정하기 페이지 유형입니다. type = %s"),
    INVALID_TODO_REQUEST_TYPE(BAD_REQUEST, "유효하지 않은 할 일 요청 유형입니다. type = %s"),

    ALREADY_ADDED_TODO(BAD_REQUEST, "이미 추가된 할 일 입니다."),
    TODO_COUNT_EXCEEDED(BAD_REQUEST, "%s 할 일은 최대 %d 개까지 추가할 수 있습니다."),
    TODO_TYPE_MISMATCH(BAD_REQUEST, "할 일의 타입과 요청 타입이 일치하지 않습니다. 할 일 타입 = %s, 요청 타입 = %s"),

    NOT_ENOUGH_RECOMMENDED_TODOS(BAD_REQUEST, "추천할 할 일이 부족합니다. 최소 5개 이상의 추천 할 일이 필요합니다."),
    SAVE_TODO_NOT_FOUND_FROM_TARGET_PARENT(NOT_FOUND, "내 즐겨찾기에 해당 할 일을 부모로 하는 할 일이 존재하지 않습니다. 해당 cursorTodoId: %d"),

    // Schedule
    SCHEDULE_NOT_FOUND(NOT_FOUND, "스케줄을 찾을 수 없습니다."),

    // FCM
    DUPLICATE_FCM_TOKEN(BAD_REQUEST, "이미 저장된 FCM Token 입니다. memberId : %d"),
    NOT_FOUND_FCM_TOKEN(NOT_FOUND, "해당 멤버의 FCM TOKEN이 존재하지 않습니다. memberId : %d"),

    // External
    NAVER_SERVICE_UNAVAILABLE(SERVICE_UNAVAILABLE, "네이버 API 서버가 장애 상태입니다."),
    KAKAO_SERVICE_UNAVAILABLE(SERVICE_UNAVAILABLE, "카카오 API 서버가 장애 상태입니다."),
    UNAVAILABLE_PUSH_SERVICE(SERVICE_UNAVAILABLE, "현재 푸시 서비스를 이용할 수 없습니다.")
    ;

    public final HttpStatus httpStatus;
    private final String message;
}