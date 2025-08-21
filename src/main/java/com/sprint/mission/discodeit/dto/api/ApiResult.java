package com.sprint.mission.discodeit.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {
    private boolean success;
    private String message;
    private T data;
    private ApiError error;

    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> ok(T data, String message) {
        return ApiResult.<T>builder()
                .message(message)
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> ok(String message) {
        return ApiResult.<T>builder()
                .message(message)
                .success(true)
                .build();
    }

    public static <T> ApiResult<T> fail(String code, String message) {
        return ApiResult.<T>builder()
                .success(false)
                .error(new ApiError(code, message))
                .build();
    }
}

