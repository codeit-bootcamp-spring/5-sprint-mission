package com.sprint.mission.discodeit.dto.common;

// 응답 결과를 일정한 포멧으로처 처리하기 위한 응답 공통 설계

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 인값은 직렬화에 포함하지 않음
public class ApiResult<T> {
    private boolean success; // 응답의 성공 여부
    private String message; // 응답 메세지
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data; // 실제 body 영역
    private ApiError error; // 에러메세지
    private Instant timestamp; // REST 응답시간


    // 공통 API를 생성하기 위한 생성 메서드
    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResult<T> ok(T data, String message) {
        return ApiResult.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResult<T> ok(String message) {
        return ApiResult.<T>builder()
                .success(true)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResult<T> fail(String code, String message) {
        return ApiResult.<T>builder()
                .success(false)
                .error(new ApiError(code, message))
                .timestamp(Instant.now())
                .build();
    }
}
