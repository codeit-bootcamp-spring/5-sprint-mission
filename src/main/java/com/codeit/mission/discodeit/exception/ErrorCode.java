package com.codeit.mission.discodeit.exception;

public enum ErrorCode {
    // User errors
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME("이미 존재하는 사용자명입니다."),
    DUPLICATE_EMAIL("이미 존재하는 이메일입니다."),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),
    INVALID_PASSWORD("잘못된 비밀번호입니다."),

    // Channel errors
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE("프라이빗 채널은 수정할 수 없습니다."),
    INVALID_CHANNEL_TYPE("잘못된 채널 타입입니다."),
    CHANNEL_ACCESS_DENIED("채널에 대한 접근 권한이 없습니다."),

    // Message errors
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),
    MESSAGE_CONTENT_EMPTY("메시지 내용이 비어있습니다."),
    MESSAGE_TOO_LONG("메시지가 너무 깁니다."),
    MESSAGE_ACCESS_DENIED("메시지에 대한 접근 권한이 없습니다."),

    // BinaryContent errors
    BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),
    FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다."),
    FILE_SIZE_EXCEEDED("파일 크기가 제한을 초과했습니다."),
    INVALID_FILE_TYPE("지원하지 않는 파일 형식입니다."),

    // ReadStatus errors
    READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
    READ_STATUS_ALREADY_EXISTS("이미 존재하는 읽음 상태입니다."),

    // UserStatus errors
    USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다."),
    USER_STATUS_ALREADY_EXISTS("이미 존재하는 사용자 상태입니다."),

    // Authentication errors
    AUTHENTICATION_FAILED("인증에 실패했습니다."),
    ACCESS_DENIED("접근이 거부되었습니다."),

    // General errors
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    VALIDATION_ERROR("유효성 검사에 실패했습니다."),
    DATABASE_ERROR("데이터베이스 오류가 발생했습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}