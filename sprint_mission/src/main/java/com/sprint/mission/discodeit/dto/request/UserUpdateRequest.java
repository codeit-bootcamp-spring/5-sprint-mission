package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min=3,max=20,message = "이름은 3자 이상 20자 이하여야 합니다")
    String newUsername,
    @Size(max = 100,message = "이메일은 100자 이하여야 합니다")
    @Email(message = "유효한 이메일 형식이어야 합니다")
    String newEmail,
    @Size(min=8,max=30,message = "비밀번호는 8자 이상 30자 이하여야 합니다")
    String newPassword
) {

}
