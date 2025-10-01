package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 3, max = 20, message = "사용자 이름은 3~20자여야 합니다.")
        String newUsername,

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String newEmail,

        @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
        String newPassword
) {

}
