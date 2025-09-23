package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // User
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DUPLICATE_USER("이미 존재하는 사용자입니다."),
    INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다."),

    // Channel
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE("비공개 채널이 업데이트되었습니다."),

    // Message
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND("바이너리 컨텐츠를 찾을 수 없습니다."),

    // ReadStatus
    READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
    DUPLICATE_READ_STATUS("이미 존재하는 읽음 상태입니다."),

    // UserStatus
    USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다."),
    DUPLICATE_USER_STATUS("이미 존재하는 사용자 상태입니다."),

    // BinaryContentStorage
    FILE_NOT_FOUND("파일을 찾을 수 없습니다."),
    DUPLICATE_FILE("이미 존재하는 파일입니다.");

    private final String message;
}
