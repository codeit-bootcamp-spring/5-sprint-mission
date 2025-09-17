package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.UNPROCESSABLE_ENTITY, "E001", "입력 값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "서버 내부 오류가 발생했습니다."),

    // 도메인별 (예: User 도메인)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자입니다."),

    // 도메인별 (예: Channel 도메인)
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "채널을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
