package com.sprint.mission.discodeit.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private final Instant timestamp;       // 에러 발생 시각
    private final String code;             // ErrorCode의 식별 코드 (예: U001, C001)
    private final String message;          // 에러 메시지
    private final Map<String, Object> details; // 도메인별 추가 정보
    private final String exceptionType;    // 발생한 예외 클래스 이름
    private final int status;              // HTTP 상태 코드
}
