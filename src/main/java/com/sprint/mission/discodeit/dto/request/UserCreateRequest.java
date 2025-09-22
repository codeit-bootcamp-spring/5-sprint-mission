package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

    @NotBlank(message = "ID는 필수입니다")
    @Size(min = 4, max = 50, message = "ID는 최소 4자부터 최대 50자까지 가능합니다")
    String username,

    @NotBlank(message = "email은 필수입니다")
    @Email
    @Size(max = 100, message = "email은 최대 100자 까지 가능합니다")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자부터 최대 20자까지 가능합니다")
    String password
) {

}
