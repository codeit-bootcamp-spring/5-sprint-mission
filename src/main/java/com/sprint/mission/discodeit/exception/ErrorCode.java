package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    USER_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "잘못된 인증 정보입니다."),

    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."),
    DUPLICATE_CHANNEL_NAME(HttpStatus.CONFLICT, "이미 존재하는 채널 이름입니다."),
    CHANNEL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "채널에 접근할 수 있는 권한이 없습니다."),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.FORBIDDEN, "개인 채널은 수정할 수 없습니다."),

    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),
    MESSAGE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "메시지에 접근할 수 있는 권한이 없습니다."),
    MESSAGE_FORBIDDEN_EDIT(HttpStatus.FORBIDDEN, "메시지를 수정할 수 있는 권한이 없습니다."),
    MESSAGE_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN, "메시지를 삭제할 수 있는 권한이 없습니다."),

    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

    INVALID_JSON(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다. JSON 형식과 필드 타입을 확인해주세요."),
    INVALID_PARAMETER_VALUE(HttpStatus.BAD_REQUEST, "요청 매개변수 값이 유효하지 않습니다."),
    INVALID_BODY_VALUE(HttpStatus.BAD_REQUEST, "요청 본문 값이 유효하지 않습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "요청 매개변수가 누락되었습니다."),
    MISSING_PART(HttpStatus.BAD_REQUEST, "요청에 필요한 요소가 포함되어 있지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    ENDPOINT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 API 엔드포인트입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않는 HTTP 메서드입니다."),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "미디어 타입이 지원되지 않습니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청이 현재 리소스 상태와 충돌합니다."),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "요청 본문 크기가 너무 큽니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = Objects.requireNonNull(httpStatus);
        this.message = Objects.requireNonNull(message);
    }
}
