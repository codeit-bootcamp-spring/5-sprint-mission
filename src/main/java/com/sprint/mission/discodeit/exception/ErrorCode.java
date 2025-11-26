package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "중복된 사용자명입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "사용자 이름 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
    USER_PROFILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 업로드에 실패했습니다."),

    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."),
    DUPLICATE_PRIVATE_CHANNEL(HttpStatus.CONFLICT, "이미 존재하는 개인 채널입니다."),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.FORBIDDEN, "개인 채널은 수정할 수 없습니다."),
    USERS_NOT_FOUND(HttpStatus.NOT_FOUND, "일부 사용자를 찾을 수 없습니다."),

    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),
    MESSAGE_FORBIDDEN_EDIT(HttpStatus.FORBIDDEN, "메시지를 수정할 수 있는 권한이 없습니다."),
    MESSAGE_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN, "메시지를 삭제할 수 있는 권한이 없습니다."),
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    BINARY_CONTENT_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    BINARY_CONTENT_STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장소 오류가 발생했습니다."),

    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "읽음 상태를 찾을 수 없습니다."),

    INVALID_JSON(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다. JSON 형식과 필드 타입을 확인해주세요."),
    INVALID_PARAMETER_VALUE(HttpStatus.BAD_REQUEST, "요청 매개변수 값이 유효하지 않습니다."),
    INVALID_BODY_VALUE(HttpStatus.BAD_REQUEST, "요청 본문 값이 유효하지 않습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "요청 매개변수가 누락되었습니다."),
    MISSING_PART(HttpStatus.BAD_REQUEST, "요청에 필요한 요소가 포함되어 있지 않습니다."),
    MISSING_COOKIE(HttpStatus.BAD_REQUEST, "필수 쿠키가 누락되었습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INSUFFICIENT_ROLE(HttpStatus.FORBIDDEN, "해당 작업을 수행할 권한이 없습니다."),
    ENDPOINT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 API 엔드포인트입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않는 HTTP 메서드입니다."),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "미디어 타입이 지원되지 않습니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청이 현재 리소스 상태와 충돌합니다."),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "요청 본문 크기가 너무 큽니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다."),

    JWT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 생성에 실패했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = Objects.requireNonNull(httpStatus);
        this.message = Objects.requireNonNull(message);
    }
}
