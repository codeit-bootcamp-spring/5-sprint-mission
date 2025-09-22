package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // User 관련 에러 코드
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID("이미 존재하는 로그인 ID입니다."),
    DUPLICATE_EMAIL("이미 존재하는 이메일입니다."),
    INVALID_PASSWORD("잘못된 비밀번호입니다."),

    // Channel 관련 에러 코드
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    DUPLICATE_CHANNEL_NAME("이미 존재하는 채널명입니다."),
    NOT_CHANNEL_MEMBER("채널의 멤버가 아닙니다."),
    PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED("비공개 채널은 변경할 수 없습니다."),
    INVALID_PARTICIPANT("유효하지 않은 참여자입니다."),

    // Message 관련 에러 코드
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),
    UNAUTHORIZED_MESSAGE_ACCESS("메시지 수정 권한이 없습니다."),

    // BinaryContent 관련 에러 코드
    BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),
    FILE_UPLOAD_ERROR("파일 업로드에 실패했습니다."),
    FILE_SIZE_EXCEEDED("파일 크기가 허용된 범위를 초과했습니다."),

    // UserStatus 관련 에러 코드
    USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다."),
    ALREADY_EXISTS_USER_STATUS("이미 존재하는 사용자 상태입니다."),

    // ReadStatus 관련 에러 코드
    READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
    ALREADY_EXISTS_READ_STATUS("이미 존재하는 읽음 상태입니다."),

    // Server 에러 코드
    INVALID_REQUEST("잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.");

    private final String message;
}