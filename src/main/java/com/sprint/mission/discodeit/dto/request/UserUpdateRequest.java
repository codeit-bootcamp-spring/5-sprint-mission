package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @Size(min = 2, max = 10, message = "사용자 이름은 2자 이상 10자 이하여야 합니다.")
    String newUsername,

    @Email(message = "유효한 이메일 형식이어야합니다.")
    @Size(max = 20, message = "이메일은 20자 이하여야합니다.")
    String newEmail,

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,}$",
        message = "비밀번호는 숫자, 문자, 특수문자를 포함해야합니다.")
    String newPassword
) {

}
