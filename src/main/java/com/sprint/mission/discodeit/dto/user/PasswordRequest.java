package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PasswordRequest(
        @NotNull(message = "아이디를 입력하세요")
        UUID userId,

        @NotBlank(message = "현재 비밀번호를 입력하세요")
        String password,

        @NotBlank(message = "새로운 비밀번호를 입력하세요")
        String newPassword
) {
}
