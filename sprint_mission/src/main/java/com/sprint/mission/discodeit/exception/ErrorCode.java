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

    // Channel 관련 에러 코드
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    PRIVATTE_CHANNEL_UPDATE("비공개 채널 업데이트 불가"),
    CHANNEL_ALREADY_EXISTS("이미 존재하는 채널입니다."),

    // Message 관련 에러 코드
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),
    MESSAGE_SEND_FAILED("메시지 전송에 실패했습니다."),
    MESSAGE_UPDATE_NOT_ALLOWED("메시지 수정이 허용되지 않습니다."),

    // File 관련 오류
    FILE_NOT_FOUND("파일을 찾을 수 없습니다."),
    FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다."),
    FILE_DOWNLOAD_FAILED("파일 다운로드에 실패했습니다."),
    FILE_TOO_LARGE("파일 크기가 허용 범위를 초과했습니다."),

    // Server 에러 코드
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다.");


    private final String message;

    public String getMessage(){
        return this.message;
    }
}
