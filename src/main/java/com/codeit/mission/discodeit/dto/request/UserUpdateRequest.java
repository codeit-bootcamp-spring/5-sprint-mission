package com.codeit.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "새 사용자명은 필수입니다.")
        @Size(min = 2, max = 50, message = "새 사용자명은 2자 이상 50자 이하여야 합니다.")
        String newUsername,

        @NotBlank(message = "새 이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 100, message = "새 이메일은 100자 이하여야 합니다.")
        String newEmail,

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Size(min = 8, max = 100, message = "새 비밀번호는 8자 이상 100자 이하여야 합니다.")
        String newPassword
) {

}
