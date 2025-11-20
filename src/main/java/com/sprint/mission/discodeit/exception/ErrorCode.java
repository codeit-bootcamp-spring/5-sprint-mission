package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
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

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
