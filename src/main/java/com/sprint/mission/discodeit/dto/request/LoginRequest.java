package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotBlank(message = "아이디는 필수 입력입니다.")
    String username,
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    String password
) {

}
