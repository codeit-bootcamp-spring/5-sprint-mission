package com.sprint.mission.discodeit.exception.base;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // USER
    USER_NOT_FOUND(404, "User not found"),
    EMAIL_ALREADY_EXISTS(409, "Email already exists"),
    USERNAME_ALREADY_EXISTS(409, "Username already exists"),
    PASSWORD_MISMATCH(409, "Password mismatch"),

    // CHANNEL
    CHANNEL_NOT_FOUND(404, "Channel not found"),
    PRIVATE_CHANNEL_UPDATE(409, "Private channel updated"),

    // MESSAGE
    MESSAGE_NOT_FOUND(404, "Message not found"),

    // USER STATUS
    USER_STATUS_NOT_FOUND(404, "User status not found"),
    USER_STATUS_ALREADY_EXISTS(409, "User status already exists"),

    // READ_STATUS
    READ_STATUS_NOT_FOUND(404, "Read status not found"),
    READ_STATUS_ALREADY_EXISTS(409, "Read status already exists"),

    // BINARY_CONTENT
    BINARY_CONTENT_NOT_FOUND(404, "Binary content not found"),
    FILE_NOT_FOUND(404, "File not found"),
    FILE_ALREADY_EXISTS(409, "File already exists"),

    // Server 에러 코드
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST(400, "잘못된 요청입니다."),

    // Security 관련 에러 코드
    INVALID_TOKEN(401, "토큰이 유효하지 않습니다."),
    INVALID_USER_DETAILS(401, "사용자 인증 정보(UserDetails)가 유효하지 않습니다."),;

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
