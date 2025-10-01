package com.sprint.mission.discodeit.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // User 관련 에러 코드
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DUPLICATE_USER("이미 존재하는 사용자입니다."),
    INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다."),
    INVALID_USER_PARAMETER("잘못된 사용자 파라미터입니다."),

    // Channer
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다."),
    DUPLICATE_CHANNEL("이미 존재하는 채널입니다."),

    // BinaryContent
    BINARYCONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),

    // Message
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),


    // ReadStatus
    READSTATUS_NOT_FOUND("ReadStatus를 찾을 수 없습니다."),
    DUPLICATE_READSTATUS("이미 존재하는 ReadStatus입니다."),

    // UserStatus
    USERSTATUS_NOT_FOUND("UserStatus를 찾을 수 없습니다."),
    DUPLICATE_USERSTATE("이미 존재하는 UserStatus입니다."),


    // Server 에러 코드
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다.");

    private final String message;
}
