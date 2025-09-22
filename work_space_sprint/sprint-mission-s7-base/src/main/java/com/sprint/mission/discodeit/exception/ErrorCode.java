package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User 관련 에러
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DUPLICATE_USER("이미 존재하는 사용자입니다."),
    
    // Channel 관련 에러
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다."),
    
    // Message 관련 에러
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),
    
    // BinaryContent 관련 에러
    BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),
    
    // Auth 관련 에러
    WRONG_PASSWORD("비밀번호가 일치하지 않습니다."),
    
    // 일반적인 에러
    INVALID_INPUT("잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.");

    private final String message;
}