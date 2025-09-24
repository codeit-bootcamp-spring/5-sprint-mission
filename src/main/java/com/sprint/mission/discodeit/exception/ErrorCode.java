package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
	DUPLICATE_USER("이미 존재하는 사용자입니다."),
	INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다."),
	INVALID_USER_PARAMETER("잘못된 사용자 파라미터입니다."),

	INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
	INVALID_REQUEST("잘못된 요청입니다."),

	FILE_PROCESSING_FAIL("파일 처리에 실패했습니다."),

	BINARY_CONTENT_NOT_FOUND("해당 데이터를 찾을 수 없습니다.");

	private final String message;

}
