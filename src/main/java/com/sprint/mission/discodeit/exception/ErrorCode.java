package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// USER
	USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
	DUPLICATE_USERNAME("이미 존재하는 사용자이름입니다."),
	DUPLICATE_USER_EMAIL("이미 존재하는 사용자 이메일입니다."),
	DUPLICATE_USERNAME_OR_EMAIL("이미 존재하는 사용자이름 또는 이메일입니다."),
	INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다."),
	INVALID_USER_PARAM("잘못된 사용자 파라매터 입니다."),

	// USER_STATUS
	USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다."),

	// AUTH
	WRONG_PASSWORD("잘못된 비밀번호입니다."),

	// Server 에러 코드
	INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
	INVALID_REQUEST("잘못된 요청입니다."),
	URI_CREATE_FAIL("URI 리소스를 만드는데 실패했습니다."),

	// FILE STORAGE
	SAVE_TO_FILE_STORAGE_FAIL("파일 스토리지 저장을 실패했습니다."),

	// BINARY CONTENT
	BINARY_CONTENT_NOT_FOUND("바이너리 파일을 찾을 수 없습니다."),
	BINARY_CONTENT_READ_FAIL("바이너리 읽는데 실패했습니다."),

	// CHANNEL
	CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
	PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED("private채널은 업데이트가 허용되지 않습니다."),
	PARTICIPANTS_EMPTY("참여자는 1명 이상이어야합니다."),

	// MESSAGE
	AUTHOR_NOT_FOUND("글쓴이를 찾을 수 없습니다."),
	MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),

	// READ STATUS
	READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
	READ_STATUS_DUPLICATE("이미 존재하는 읽음 상태입니다."),
	
	// VALIDATION_ERROR
	VALIDATION_ERROR("요청값이 잘못되었습니다.");

	private final String message;
}
